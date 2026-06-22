package com.scaffold.audio;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RcsMapperTest {

    @TempDir
    Path tempDir;

    private final RcsMapper mapper = new RcsMapper();

    @AfterEach
    void tearDown() {
        mapper.closeDataSources();
    }

    @Test
    void addOrUpdateInsertsAndUpdatesById() {
        String dbPath = tempDir.resolve("rcs.db").toString();
        RcsDB db = rcs(1, 10.0, 1.5, validRcsBlob());

        mapper.addOrUpdate(dbPath, db);
        mapper.addOrUpdate(dbPath, rcs(1, 20.0, 2.5, null));

        List<RcsDB> rows = mapper.selectByPath(dbPath, new RcsDB(1, null, null, null, null, null, null, null, null, null, null, null, null, null, null));

        assertThat(rows).hasSize(1);
        assertThat(rows.getFirst().getFrequency()).isEqualTo(20.0);
        assertThat(rows.getFirst().getNormalRcs()).isEqualTo(2.5);
        assertThat(rows.getFirst().getRcsData()).isEmpty();
    }

    @Test
    void selectByPathParsesValidRcsBlob() {
        String dbPath = tempDir.resolve("rcs.db").toString();
        mapper.addOrUpdate(dbPath, rcs(1, 10.0, 1.5, validRcsBlob()));

        RcsDB row = mapper.selectByPath(dbPath, null).getFirst();

        assertThat(row.getRcsData()).hasDimensions(361, 181);
        assertThat(row.getRcsData()[0][0]).isEqualTo(0.0f);
        assertThat(row.getRcsData()[0][1]).isEqualTo(1.0f);
    }

    @Test
    void selectSummaryByPathDoesNotLoadBlob() {
        String dbPath = tempDir.resolve("rcs.db").toString();
        mapper.addOrUpdate(dbPath, rcs(1, 10.0, 1.5, validRcsBlob()));

        RcsDB row = mapper.selectSummaryByPath(dbPath, null).getFirst();

        assertThat(row.getRcsList()).isNull();
        assertThat(row.getRcsData()).isEmpty();
        assertThat(row.getFrequency()).isEqualTo(10.0);
    }

    @Test
    void deleteByPathRejectsEmptyCondition() {
        String dbPath = tempDir.resolve("rcs.db").toString();
        mapper.addOrUpdate(dbPath, rcs(1, 10.0, 1.5, null));

        assertThatThrownBy(() -> mapper.deleteByPath(dbPath, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("删除条件不能为空");

        assertThat(mapper.selectByPath(dbPath, null)).hasSize(1);
    }

    private static RcsDB rcs(Integer id, Double frequency, Double normalRcs, byte[] rcsList) {
        return new RcsDB(id, 1, frequency, 0.0, 180.0, 0.0, 360.0, 1.0, 1.0, 181, 361, rcsList == null ? 0 : rcsList.length, rcsList, null, normalRcs);
    }

    private static byte[] validRcsBlob() {
        ByteBuffer buffer = ByteBuffer.allocate(361 * 181 * Float.BYTES).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < 361 * 181; i++) {
            buffer.putFloat(i);
        }
        return buffer.array();
    }
}
