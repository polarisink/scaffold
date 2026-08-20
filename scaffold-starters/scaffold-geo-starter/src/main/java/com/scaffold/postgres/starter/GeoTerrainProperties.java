package com.scaffold.postgres.starter;

import com.scaffold.postgres.region.GeoRegion;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Locale;

/**
 * 地形组件配置
 *
 * @param enabled                  是否启用
 * @param provinceBoundaryLocation
 * @param demBaseLocation
 * @param demFileNamePattern
 * @param maximumCachedSources
 * @param pointCacheEnabled
 * @param maximumCachedPoints
 * @param coordinateDecimalPlaces
 * @param cacheMissingElevations
 */
@ConfigurationProperties(prefix = GeoTerrainProperties.PREFIX)
public record GeoTerrainProperties(
        Boolean enabled,
        String provinceBoundaryLocation,
        String demBaseLocation,
        String demFileNamePattern,
        int maximumCachedSources,
        Boolean pointCacheEnabled,
        int maximumCachedPoints,
        int coordinateDecimalPlaces,
        Boolean cacheMissingElevations) {

    public static final String PREFIX = "scaffold.geo";


    public GeoTerrainProperties {
        if (enabled == null) {
            enabled = true;
        }
        if (provinceBoundaryLocation == null) {
            provinceBoundaryLocation = "classpath:/scaffold/geo/province-boundaries.csv";
        }
        if (demBaseLocation == null) {
            demBaseLocation = "file:./dem/";
        }
        if (demFileNamePattern == null) {
            demFileNamePattern = "{id-lower}.tif";
        }
        if (maximumCachedSources == 0) {
            maximumCachedSources = 5;
        }
        if (pointCacheEnabled == null) {
            pointCacheEnabled = true;
        }
        if (maximumCachedPoints <= 0) {
            maximumCachedPoints = 100000;
        }
        if (coordinateDecimalPlaces <= 0) {
            coordinateDecimalPlaces = 6;
        }
        if (cacheMissingElevations == null) {
            cacheMissingElevations = false;
        }
    }

    /**
     * 根据区域信息解析 DEM 文件名。
     */
    public String resolveDemFileName(GeoRegion region) {
        return demFileNamePattern
                .replace("{id-lower}", region.id().toLowerCase(Locale.ROOT))
                .replace("{id}", region.id())
                .replace("{name}", region.name());
    }
}
