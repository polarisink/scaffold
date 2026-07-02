package com.scaffold.geo.terrain;

/**
 * 点级高程缓存统计快照。
 *
 * @param totalRequests 查询总数
 * @param cacheHits     缓存命中数
 * @param cacheMisses   缓存未命中数
 * @param cachedPoints  当前缓存点数
 * @param hitRate       缓存命中率，范围为 [0, 1]
 */
public record ElevationCacheStatistics(long totalRequests, long cacheHits, long cacheMisses,
                                       int cachedPoints, double hitRate) {
}
