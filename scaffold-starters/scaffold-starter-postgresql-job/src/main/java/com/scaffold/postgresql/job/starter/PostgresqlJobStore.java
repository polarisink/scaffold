package com.scaffold.postgresql.job.starter;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class PostgresqlJobStore {

    private static final Pattern SQL_IDENTIFIER_PATTERN = Pattern.compile(
            "[A-Za-z_][A-Za-z0-9_]*(\\.[A-Za-z_][A-Za-z0-9_]*)?"
    );
    private static final Pattern CHANNEL_PATTERN = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");

    private final JdbcTemplate jdbcTemplate;
    private final ScaffoldJobProperties properties;

    public void initializeSchema() {
        String tableName = tableName();
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS %s (
                    id BIGSERIAL PRIMARY KEY,
                    queue_name TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'pending',
                    payload JSONB NOT NULL,
                    attempts INTEGER NOT NULL DEFAULT 0,
                    max_attempts INTEGER NOT NULL DEFAULT 3,
                    priority INTEGER NOT NULL DEFAULT 0,
                    available_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    locked_at TIMESTAMPTZ,
                    locked_until TIMESTAMPTZ,
                    worker_id TEXT,
                    last_error TEXT,
                    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    completed_at TIMESTAMPTZ
                )
                """.formatted(tableName));
        jdbcTemplate.execute("""
                CREATE INDEX IF NOT EXISTS %s_ready_idx
                ON %s (queue_name, status, available_at, priority DESC, created_at, id)
                """.formatted(indexPrefix(tableName), tableName));
        jdbcTemplate.execute("""
                CREATE INDEX IF NOT EXISTS %s_locked_until_idx
                ON %s (locked_until)
                WHERE status = 'processing'
                """.formatted(indexPrefix(tableName), tableName));
    }

    public long enqueue(String queueName, String payload, int priority, int maxAttempts) {
        Long id = jdbcTemplate.queryForObject(
                "INSERT INTO " + tableName()
                        + " (queue_name, payload, priority, max_attempts)"
                        + " VALUES (?, ?::jsonb, ?, ?)"
                        + " RETURNING id",
                Long.class,
                queueName,
                payload,
                priority,
                maxAttempts
        );
        publishNotify(String.valueOf(id));
        return id == null ? 0 : id;
    }

    public Optional<PostgresqlJob> claimNext(List<String> queueNames, String workerId, Duration visibilityTimeout) {
        List<Object> args = new ArrayList<>();
        String queueFilter = "";
        if (queueNames != null && !queueNames.isEmpty()) {
            queueFilter = " queue_name IN (" + placeholders(queueNames.size()) + ") AND";
            args.addAll(queueNames);
        }
        args.add(workerId);
        args.add(Math.max(1, visibilityTimeout.toMillis()));
        List<PostgresqlJob> jobs = jdbcTemplate.query("""
                WITH next_job AS (
                    SELECT id
                    FROM %s
                    WHERE%s (
                        (status = 'pending' AND available_at <= CURRENT_TIMESTAMP)
                        OR (status = 'processing' AND locked_until <= CURRENT_TIMESTAMP)
                    )
                    ORDER BY priority DESC, created_at, id
                    LIMIT 1
                    FOR UPDATE SKIP LOCKED
                )
                UPDATE %s job
                SET status = 'processing',
                    attempts = job.attempts + 1,
                    worker_id = ?,
                    locked_at = CURRENT_TIMESTAMP,
                    locked_until = CURRENT_TIMESTAMP + (? * INTERVAL '1 millisecond'),
                    updated_at = CURRENT_TIMESTAMP
                FROM next_job
                WHERE job.id = next_job.id
                RETURNING job.*
                """.formatted(tableName(), queueFilter, tableName()), JOB_ROW_MAPPER, args.toArray());
        return jobs.stream().findFirst();
    }

    public boolean complete(long jobId, String workerId) {
        return jdbcTemplate.update(
                "UPDATE " + tableName()
                        + " SET status = 'completed', completed_at = CURRENT_TIMESTAMP,"
                        + " locked_at = NULL, locked_until = NULL, worker_id = NULL,"
                        + " updated_at = CURRENT_TIMESTAMP"
                        + " WHERE id = ? AND worker_id = ? AND status = 'processing'",
                jobId,
                workerId
        ) == 1;
    }

    public boolean fail(PostgresqlJob job, String workerId, String error, Duration retryDelay) {
        if (job.getAttempts() >= job.getMaxAttempts()) {
            return jdbcTemplate.update(
                    "UPDATE " + tableName()
                            + " SET status = 'failed', last_error = ?, locked_at = NULL,"
                            + " locked_until = NULL, worker_id = NULL, updated_at = CURRENT_TIMESTAMP"
                            + " WHERE id = ? AND worker_id = ? AND status = 'processing'",
                    trimError(error),
                    job.getId(),
                    workerId
            ) == 1;
        }
        boolean updated = jdbcTemplate.update(
                "UPDATE " + tableName()
                        + " SET status = 'pending', last_error = ?, available_at = CURRENT_TIMESTAMP + (? * INTERVAL '1 millisecond'),"
                        + " locked_at = NULL, locked_until = NULL, worker_id = NULL, updated_at = CURRENT_TIMESTAMP"
                        + " WHERE id = ? AND worker_id = ? AND status = 'processing'",
                trimError(error),
                Math.max(1, retryDelay.toMillis()),
                job.getId(),
                workerId
        ) == 1;
        if (updated) {
            publishNotify(String.valueOf(job.getId()));
        }
        return updated;
    }

    public void publishNotify(String payload) {
        jdbcTemplate.query("SELECT pg_notify(?, ?)", rs -> null, channel(), payload);
    }

    String tableName() {
        Assert.hasText(properties.getTableName(), "PostgreSQL job table name must not be empty");
        Assert.isTrue(SQL_IDENTIFIER_PATTERN.matcher(properties.getTableName()).matches(),
                "PostgreSQL job table name must be a plain SQL identifier or schema-qualified identifier");
        return properties.getTableName();
    }

    String channel() {
        Assert.hasText(properties.getNotifyChannel(), "PostgreSQL job notify channel must not be empty");
        Assert.isTrue(CHANNEL_PATTERN.matcher(properties.getNotifyChannel()).matches(),
                "PostgreSQL job notify channel must be a plain SQL identifier");
        return properties.getNotifyChannel();
    }

    private String placeholders(int size) {
        return String.join(", ", java.util.Collections.nCopies(size, "?"));
    }

    private String indexPrefix(String tableName) {
        return tableName.replace('.', '_');
    }

    private String trimError(String error) {
        if (error == null) {
            return null;
        }
        return error.length() <= 4000 ? error : error.substring(0, 4000);
    }

    private static final RowMapper<PostgresqlJob> JOB_ROW_MAPPER = new RowMapper<>() {
        @Override
        public PostgresqlJob mapRow(ResultSet rs, int rowNum) throws SQLException {
            PostgresqlJob job = new PostgresqlJob();
            job.setId(rs.getLong("id"));
            job.setQueueName(rs.getString("queue_name"));
            job.setStatus(PostgresqlJobStatus.valueOf(rs.getString("status").toUpperCase()));
            job.setPayload(rs.getString("payload"));
            job.setAttempts(rs.getInt("attempts"));
            job.setMaxAttempts(rs.getInt("max_attempts"));
            job.setPriority(rs.getInt("priority"));
            job.setAvailableAt(rs.getObject("available_at", java.time.OffsetDateTime.class));
            job.setLockedAt(rs.getObject("locked_at", java.time.OffsetDateTime.class));
            job.setLockedUntil(rs.getObject("locked_until", java.time.OffsetDateTime.class));
            job.setWorkerId(rs.getString("worker_id"));
            job.setLastError(rs.getString("last_error"));
            job.setCreatedAt(rs.getObject("created_at", java.time.OffsetDateTime.class));
            job.setUpdatedAt(rs.getObject("updated_at", java.time.OffsetDateTime.class));
            job.setCompletedAt(rs.getObject("completed_at", java.time.OffsetDateTime.class));
            return job;
        }
    };
}
