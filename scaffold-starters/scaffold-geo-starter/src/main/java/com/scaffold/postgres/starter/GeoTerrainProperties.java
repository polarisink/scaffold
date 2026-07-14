package com.scaffold.postgres.starter;

import com.scaffold.postgres.region.GeoRegion;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.Locale;

/**
 * 地形组件配置。
 */
@ConfigurationProperties(prefix = GeoTerrainProperties.PREFIX)
public record GeoTerrainProperties(
        @DefaultValue("true") boolean enabled,
        @DefaultValue("classpath:/scaffold/geo/province-boundaries.csv") String provinceBoundaryLocation,
        @DefaultValue("file:./dem/") String demBaseLocation,
        @DefaultValue("{id-lower}.tif") String demFileNamePattern,
        @DefaultValue("5") int maximumCachedSources,
        @DefaultValue("true") boolean pointCacheEnabled,
        @DefaultValue("100000") int maximumCachedPoints,
        @DefaultValue("6") int coordinateDecimalPlaces,
        @DefaultValue("false") boolean cacheMissingElevations) {

    public static final String PREFIX = "scaffold.geo";

    /**
     * 是否启用地形组件。
     */

    /**
     * 省份边界 CSV 的 Spring 资源位置。
     */

    /**
     * DEM 文件目录的 Spring 资源位置。
     */

    /**
     * DEM 文件名模板，支持 {id}、{id-lower} 和 {name} 占位符。
     */

    /**
     * GeoTIFF 数据源的最大缓存数量。
     */

    /**
     * 是否启用点级高程缓存。
     */

    /**
     * 点级高程缓存的最大坐标数量。
     */

    /**
     * 点级缓存坐标保留的小数位数。
     */

    /**
     * 是否缓存未查询到高程的坐标。
     */

    /**
     * 根据区域信息解析 DEM 文件名。
     */
    public String resolveDemFileName(GeoRegion region) {
        return demFileNamePattern
                .replace("{id-lower}", region.id().toLowerCase(Locale.ROOT))
                .replace("{id}", region.id())
                .replace("{name}", region.name());
    }

    public int getMaximumCachedPoints() {
        return maximumCachedPoints;
    }


    public boolean isEnabled() {
        return enabled;
    }


    public String getProvinceBoundaryLocation() {
        return provinceBoundaryLocation;
    }


    public String getDemBaseLocation() {
        return demBaseLocation;
    }


    public String getDemFileNamePattern() {
        return demFileNamePattern;
    }


    public int getMaximumCachedSources() {
        return maximumCachedSources;
    }


    public boolean isPointCacheEnabled() {
        return pointCacheEnabled;
    }


    public int getCoordinateDecimalPlaces() {
        return coordinateDecimalPlaces;
    }


    public boolean isCacheMissingElevations() {
        return cacheMissingElevations;
    }

}
