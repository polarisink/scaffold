package com.scaffold.geo.starter;

import com.scaffold.geo.region.GeoRegion;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Locale;

/**
 * 地形组件配置。
 */
@ConfigurationProperties(prefix = GeoTerrainProperties.PREFIX)
public class GeoTerrainProperties {

    public static final String PREFIX = "scaffold.geo";

    /**
     * 是否启用地形组件。
     */
    private boolean enabled = true;

    /**
     * 省份边界 CSV 的 Spring 资源位置。
     */
    private String provinceBoundaryLocation = "classpath:/scaffold/geo/province-boundaries.csv";

    /**
     * DEM 文件目录的 Spring 资源位置。
     */
    private String demBaseLocation = "file:./dem/";

    /**
     * DEM 文件名模板，支持 {id}、{id-lower} 和 {name} 占位符。
     */
    private String demFileNamePattern = "{id-lower}.tif";

    /**
     * GeoTIFF 数据源的最大缓存数量。
     */
    private int maximumCachedSources = 5;

    /**
     * 是否启用点级高程缓存。
     */
    private boolean pointCacheEnabled = true;

    /**
     * 点级高程缓存的最大坐标数量。
     */
    private int maximumCachedPoints = 100_000;

    /**
     * 点级缓存坐标保留的小数位数。
     */
    private int coordinateDecimalPlaces = 6;

    /**
     * 是否缓存未查询到高程的坐标。
     */
    private boolean cacheMissingElevations;

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

    public void setMaximumCachedPoints(int maximumCachedPoints) {
        this.maximumCachedPoints = maximumCachedPoints;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getProvinceBoundaryLocation() {
        return provinceBoundaryLocation;
    }

    public void setProvinceBoundaryLocation(String provinceBoundaryLocation) {
        this.provinceBoundaryLocation = provinceBoundaryLocation;
    }

    public String getDemBaseLocation() {
        return demBaseLocation;
    }

    public void setDemBaseLocation(String demBaseLocation) {
        this.demBaseLocation = demBaseLocation;
    }

    public String getDemFileNamePattern() {
        return demFileNamePattern;
    }

    public void setDemFileNamePattern(String demFileNamePattern) {
        this.demFileNamePattern = demFileNamePattern;
    }

    public int getMaximumCachedSources() {
        return maximumCachedSources;
    }

    public void setMaximumCachedSources(int maximumCachedSources) {
        this.maximumCachedSources = maximumCachedSources;
    }

    public boolean isPointCacheEnabled() {
        return pointCacheEnabled;
    }

    public void setPointCacheEnabled(boolean pointCacheEnabled) {
        this.pointCacheEnabled = pointCacheEnabled;
    }

    public int getCoordinateDecimalPlaces() {
        return coordinateDecimalPlaces;
    }

    public void setCoordinateDecimalPlaces(int coordinateDecimalPlaces) {
        this.coordinateDecimalPlaces = coordinateDecimalPlaces;
    }

    public boolean isCacheMissingElevations() {
        return cacheMissingElevations;
    }

    public void setCacheMissingElevations(boolean cacheMissingElevations) {
        this.cacheMissingElevations = cacheMissingElevations;
    }
}
