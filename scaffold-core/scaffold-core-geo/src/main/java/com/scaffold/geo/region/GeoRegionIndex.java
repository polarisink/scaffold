package com.scaffold.geo.region;

import com.scaffold.geo.GeoCoordinate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 有序的区域边界框索引。边界框重叠时，返回第一个匹配的区域。
 */
public record GeoRegionIndex(List<GeoRegion> regions) {

    public GeoRegionIndex {
        regions = List.copyOf(regions);
    }

    public Optional<GeoRegion> find(GeoCoordinate coordinate) {
        return regions.stream().filter(region -> region.contains(coordinate)).findFirst();
    }

    /**
     * 从 CSV 读取区域，字段顺序为：区域标识、区域名称、最小经度、最小纬度、最大经度、最大纬度。
     */
    public static GeoRegionIndex fromCsv(InputStream input) throws IOException {
        if (input == null) throw new IllegalArgumentException("input must not be null");
        List<GeoRegion> regions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmed = line.strip();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
                String[] parts = trimmed.split(",", -1);
                if (parts.length != 6) {
                    throw new IOException("invalid region CSV at line " + lineNumber + ": expected 6 columns");
                }
                try {
                    regions.add(new GeoRegion(parts[0].strip(), parts[1].strip(),
                            Double.parseDouble(parts[2].strip()), Double.parseDouble(parts[3].strip()),
                            Double.parseDouble(parts[4].strip()), Double.parseDouble(parts[5].strip())));
                } catch (IllegalArgumentException exception) {
                    throw new IOException("invalid region CSV at line " + lineNumber, exception);
                }
            }
        }
        return new GeoRegionIndex(regions);
    }

}
