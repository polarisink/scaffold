package com.scaffold.geo.region;

import com.scaffold.geo.GeoCoordinate;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class GeoRegionIndexTest {

    @Test
    void readsTheBoundaryFormatUsedByBothSourceProjects() throws Exception {
        String csv = """
                # id,name,min longitude,min latitude,max longitude,max latitude
                CN-BJ,北京市,116.1,39.7,116.6,40.2
                CN-SH,上海市,121.2,30.9,121.8,31.4
                """;

        GeoRegionIndex index = GeoRegionIndex.fromCsv(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));
        assertThat(index.find(new GeoCoordinate(39.9, 116.4)))
                .get().extracting(GeoRegion::id).isEqualTo("CN-BJ");
        assertThat(index.find(new GeoCoordinate(0.0, 0.0))).isEmpty();
    }

}
