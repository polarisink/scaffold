package com.scaffold.audio;

import cn.hutool.core.lang.Assert;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 操作sqlite的rcs数据
 */
@Slf4j
@Service
public class RcsMapper implements DisposableBean {
    private static final String DRIVER_CLASS_NAME = "org.sqlite.JDBC";
    private static final int THETA_COUNT = 361;
    private static final int PHI_COUNT = 181;
    private static final int FLOAT_BYTES = Float.BYTES;
    private static final int RCS_LIST_BYTES = THETA_COUNT * PHI_COUNT * FLOAT_BYTES;
    private final Map<String, SqliteTemplateHolder> jdbcTemplateMap = new ConcurrentHashMap<>();

    public List<RcsDB> selectByPath(String path, RcsDB condition) {
        JdbcTemplate jdbcTemplate = getJdbcTemplate(path);
        StringBuilder sql = new StringBuilder("select * from data_tb");
        List<Object> args = new ArrayList<>();
        appendCondition(sql, args, condition);
        return jdbcTemplate.query(sql.toString(), RCS_DATA_ROW_MAPPER, args.toArray());
    }

    public List<RcsDB> selectSummaryByPath(String path, RcsDB condition) {
        JdbcTemplate jdbcTemplate = getJdbcTemplate(path);
        StringBuilder sql = new StringBuilder("""
                select Id, PolarizationMode, Frequency,
                       StartOfTheta, EndOfTheta, StartOfPhi, EndOfPhi,
                       ThetaStep, PhiStep, ThetaCount, PhiCount,
                       rcssize, NormalRCS
                from data_tb
                """);
        List<Object> args = new ArrayList<>();
        appendCondition(sql, args, condition);
        return jdbcTemplate.query(sql.toString(), RCS_SUMMARY_ROW_MAPPER, args.toArray());
    }

    public void deleteByPath(String path, RcsDB condition) {
        JdbcTemplate jdbcTemplate = getJdbcTemplate(path);
        StringBuilder sql = new StringBuilder("delete from data_tb");
        List<Object> args = new ArrayList<>();
        appendCondition(sql, args, condition);
        Assert.isTrue(!args.isEmpty(), "删除条件不能为空，拒绝删除全表");
        jdbcTemplate.update(sql.toString(), args.toArray());
    }

    public void addOrUpdate(String path, RcsDB db) {
        Assert.notNull(db, "RCS 数据不能为空");
        JdbcTemplate jdbcTemplate = getOrCreateJdbcTemplate(path);
        if (db.getId() == null) {
            insertWithoutId(jdbcTemplate, db);
            return;
        }
        upsertWithId(jdbcTemplate, db);
    }

    private void insertWithoutId(JdbcTemplate jdbcTemplate, RcsDB db) {
        jdbcTemplate.update("""
                insert into data_tb (
                    PolarizationMode, Frequency,
                    StartOfTheta, EndOfTheta, StartOfPhi, EndOfPhi,
                    ThetaStep, PhiStep, ThetaCount, PhiCount,
                    rcssize, RCSList, NormalRCS
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, db.getPolarizationMode(), db.getFrequency(), db.getStartOfTheta(), db.getEndOfTheta(), db.getStartOfPhi(), db.getEndOfPhi(), db.getThetaStep(), db.getPhiStep(), db.getThetaCount(), db.getPhiCount(), db.getRcsSize(), db.getRcsList(), db.getNormalRcs());
    }

    private void upsertWithId(JdbcTemplate jdbcTemplate, RcsDB db) {
        jdbcTemplate.update("""
                insert into data_tb (
                    Id, PolarizationMode, Frequency,
                    StartOfTheta, EndOfTheta, StartOfPhi, EndOfPhi,
                    ThetaStep, PhiStep, ThetaCount, PhiCount,
                    rcssize, RCSList, NormalRCS
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict(Id) do update set
                    PolarizationMode = excluded.PolarizationMode,
                    Frequency = excluded.Frequency,
                    StartOfTheta = excluded.StartOfTheta,
                    EndOfTheta = excluded.EndOfTheta,
                    StartOfPhi = excluded.StartOfPhi,
                    EndOfPhi = excluded.EndOfPhi,
                    ThetaStep = excluded.ThetaStep,
                    PhiStep = excluded.PhiStep,
                    ThetaCount = excluded.ThetaCount,
                    PhiCount = excluded.PhiCount,
                    rcssize = excluded.rcssize,
                    RCSList = excluded.RCSList,
                    NormalRCS = excluded.NormalRCS
                """, db.getId(), db.getPolarizationMode(), db.getFrequency(), db.getStartOfTheta(), db.getEndOfTheta(), db.getStartOfPhi(), db.getEndOfPhi(), db.getThetaStep(), db.getPhiStep(), db.getThetaCount(), db.getPhiCount(), db.getRcsSize(), db.getRcsList(), db.getNormalRcs());
    }

    private void ensureDataTable(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute("""
                create table if not exists data_tb (
                    Id integer primary key,
                    PolarizationMode integer,
                    Frequency real,
                    StartOfTheta real,
                    EndOfTheta real,
                    StartOfPhi real,
                    EndOfPhi real,
                    ThetaStep real,
                    PhiStep real,
                    ThetaCount integer,
                    PhiCount integer,
                    rcssize integer,
                    RCSList blob,
                    NormalRCS real
                )
                """);
    }

    private void appendCondition(StringBuilder sql, List<Object> args, RcsDB condition) {
        if (condition == null) {
            return;
        }
        List<String> where = new ArrayList<>();
        addEquals(where, args, "Id", condition.getId());
        addEquals(where, args, "PolarizationMode", condition.getPolarizationMode());
        addEquals(where, args, "Frequency", condition.getFrequency());
        addEquals(where, args, "StartOfTheta", condition.getStartOfTheta());
        addEquals(where, args, "EndOfTheta", condition.getEndOfTheta());
        addEquals(where, args, "StartOfPhi", condition.getStartOfPhi());
        addEquals(where, args, "EndOfPhi", condition.getEndOfPhi());
        addEquals(where, args, "ThetaStep", condition.getThetaStep());
        addEquals(where, args, "PhiStep", condition.getPhiStep());
        addEquals(where, args, "ThetaCount", condition.getThetaCount());
        addEquals(where, args, "PhiCount", condition.getPhiCount());
        addEquals(where, args, "rcssize", condition.getRcsSize());
        addEquals(where, args, "NormalRCS", condition.getNormalRcs());
        if (!where.isEmpty()) {
            sql.append(" where ").append(String.join(" and ", where));
        }
    }

    private void addEquals(List<String> where, List<Object> args, String column, Object value) {
        if (value == null) {
            return;
        }
        where.add(column + " = ?");
        args.add(value);
    }

    // 包内可见，用于测试
    JdbcTemplate getJdbcTemplate(String dbPath) {
        Path path = normalizePath(dbPath);
        Assert.isTrue(Files.exists(path), "数据库文件不存在:" + dbPath);
        return createJdbcTemplate(path).jdbcTemplate();
    }

    JdbcTemplate getOrCreateJdbcTemplate(String dbPath) {
        Path path = normalizePath(dbPath);
        try {
            Assert.isTrue(!Files.isDirectory(path), "数据库路径不能是目录:" + dbPath);
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (Files.notExists(path)) {
                Files.createFile(path);
                log.info("数据库文件不存在，已创建:{}", dbPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("数据库文件创建失败:" + dbPath, e);
        }
        return createJdbcTemplate(path).jdbcTemplate();
    }

    // 包内可见，用于测试
    SqliteTemplateHolder createJdbcTemplate(Path dbPath) {
        return jdbcTemplateMap.computeIfAbsent(dbPath.toString(), d -> {
            HikariConfig config = new HikariConfig();
            config.setDriverClassName(DRIVER_CLASS_NAME);
            config.setJdbcUrl("jdbc:sqlite:" + dbPath);
            config.setMaximumPoolSize(1);
            config.setMinimumIdle(0);
            config.setPoolName("sqlite-rcs-" + Math.abs(dbPath.toString().hashCode()));
            config.setConnectionInitSql("PRAGMA busy_timeout = 5000");
            HikariDataSource dataSource = new HikariDataSource(config);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.execute("PRAGMA journal_mode = WAL");
            jdbcTemplate.execute("PRAGMA synchronous = NORMAL");
            ensureDataTable(jdbcTemplate);
            return new SqliteTemplateHolder(jdbcTemplate, dataSource);
        });
    }

    private Path normalizePath(String dbPath) {
        Assert.isTrue(dbPath != null && !dbPath.isBlank(), "数据库文件路径不能为空");
        return Paths.get(dbPath).toAbsolutePath().normalize();
    }

    private static final RowMapper<RcsDB> RCS_DATA_ROW_MAPPER = (rs, row) -> {
        byte[] bytes = rs.getBytes("RCSList");
        return mapRow(rs.getInt("Id"), rs.getInt("PolarizationMode"), rs.getDouble("Frequency"),
                rs.getDouble("StartOfTheta"), rs.getDouble("EndOfTheta"), rs.getDouble("StartOfPhi"),
                rs.getDouble("EndOfPhi"), rs.getDouble("ThetaStep"), rs.getDouble("PhiStep"),
                rs.getInt("ThetaCount"), rs.getInt("PhiCount"), rs.getInt("rcssize"),
                bytes, parseRcsData(bytes), rs.getDouble("NormalRCS"));
    };

    private static final RowMapper<RcsDB> RCS_SUMMARY_ROW_MAPPER = (rs, row) -> mapRow(
            rs.getInt("Id"), rs.getInt("PolarizationMode"), rs.getDouble("Frequency"),
            rs.getDouble("StartOfTheta"), rs.getDouble("EndOfTheta"), rs.getDouble("StartOfPhi"),
            rs.getDouble("EndOfPhi"), rs.getDouble("ThetaStep"), rs.getDouble("PhiStep"),
            rs.getInt("ThetaCount"), rs.getInt("PhiCount"), rs.getInt("rcssize"),
            null, new float[0][0], rs.getDouble("NormalRCS"));

    private static RcsDB mapRow(Integer id, Integer polarizationMode, Double frequency, Double startOfTheta,
                                Double endOfTheta, Double startOfPhi, Double endOfPhi, Double thetaStep,
                                Double phiStep, Integer thetaCount, Integer phiCount, Integer rcsSize,
                                byte[] rcsList, float[][] rcsData, Double normalRcs) {
        return new RcsDB(id, polarizationMode, frequency, startOfTheta, endOfTheta, startOfPhi, endOfPhi, thetaStep, phiStep, thetaCount, phiCount, rcsSize, rcsList, rcsData, normalRcs);
    }

    private static float[][] parseRcsData(byte[] bytes) {
        if (bytes == null || bytes.length != RCS_LIST_BYTES) {
            return new float[0][0];
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        float[][] data = new float[THETA_COUNT][PHI_COUNT];
        for (int i = 0; i < THETA_COUNT; i++) {
            for (int j = 0; j < PHI_COUNT; j++) {
                data[i][j] = buffer.getFloat();
            }
        }
        return data;
    }

    @Override
    public void destroy() throws Exception {
        jdbcTemplateMap.values().forEach(SqliteTemplateHolder::close);
        jdbcTemplateMap.clear();
    }

    record SqliteTemplateHolder(JdbcTemplate jdbcTemplate, HikariDataSource dataSource) {
        void close() {
            dataSource.close();
        }
    }
}
