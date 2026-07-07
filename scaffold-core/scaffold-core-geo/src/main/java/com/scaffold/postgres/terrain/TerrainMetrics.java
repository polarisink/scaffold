package com.scaffold.postgres.terrain;

/**
 * 提供地形组件的只读运行统计。
 */
public final class TerrainMetrics {

    private final CachingElevationProvider cachingElevationProvider;
    private final GeoTiffElevationProvider geoTiffElevationProvider;

    public TerrainMetrics(CachingElevationProvider cachingElevationProvider,
                          GeoTiffElevationProvider geoTiffElevationProvider) {
        this.cachingElevationProvider = cachingElevationProvider;
        this.geoTiffElevationProvider = geoTiffElevationProvider;
    }

    /**
     * 获取当前统计快照。
     */
    public TerrainStatistics snapshot() {
        return new TerrainStatistics(cachingElevationProvider.statistics(),
                geoTiffElevationProvider.cachedSourceCount());
    }
}
