package com.scaffold.geo.terrain;

/**
 * 地形组件运行统计快照。
 *
 * @param elevationCache   点级高程缓存统计
 * @param loadedDemSources 当前已加载的 DEM 数据源数量
 */
public record TerrainStatistics(ElevationCacheStatistics elevationCache, int loadedDemSources) {
}
