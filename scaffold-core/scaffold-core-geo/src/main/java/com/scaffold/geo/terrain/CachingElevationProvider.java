package com.scaffold.geo.terrain;

import com.scaffold.geo.GeoCoordinate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 使用有界 LRU 缓存保存坐标点高程查询结果。
 */
public final class CachingElevationProvider implements ElevationProvider {

    private final ElevationProvider delegate;
    private final int maximumCachedPoints;
    private final double coordinateScale;
    private final boolean cacheMissingElevations;
    private final Map<CoordinateKey, CacheValue> cache = new LinkedHashMap<>(16, 0.75f, true);
    private final AtomicLong totalRequests = new AtomicLong();
    private final AtomicLong cacheHits = new AtomicLong();
    private final AtomicLong cacheMisses = new AtomicLong();

    public CachingElevationProvider(ElevationProvider delegate, int maximumCachedPoints, int coordinateDecimalPlaces, boolean cacheMissingElevations) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        if (maximumCachedPoints < 1) {
            throw new IllegalArgumentException("maximumCachedPoints must be at least 1");
        }
        if (coordinateDecimalPlaces < 0 || coordinateDecimalPlaces > 7) {
            throw new IllegalArgumentException("coordinateDecimalPlaces must be between 0 and 7");
        }
        this.maximumCachedPoints = maximumCachedPoints;
        this.coordinateScale = Math.pow(10.0, coordinateDecimalPlaces);
        this.cacheMissingElevations = cacheMissingElevations;
    }

    @Override
    public OptionalDouble findElevationMetres(GeoCoordinate coordinate) {
        Objects.requireNonNull(coordinate, "coordinate");
        totalRequests.incrementAndGet();
        CoordinateKey key = CoordinateKey.from(coordinate, coordinateScale);
        synchronized (cache) {
            CacheValue cached = cache.get(key);
            if (cached != null) {
                cacheHits.incrementAndGet();
                return cached.toOptionalDouble();
            }
        }

        cacheMisses.incrementAndGet();
        OptionalDouble elevation = delegate.findElevationMetres(coordinate);
        if (elevation.isPresent() || cacheMissingElevations) {
            synchronized (cache) {
                cache.put(key, CacheValue.from(elevation));
                evictIfNecessary();
            }
        }
        return elevation;
    }

    /**
     * 清空点级缓存和统计计数。
     */
    public void clearCache() {
        synchronized (cache) {
            cache.clear();
        }
        totalRequests.set(0L);
        cacheHits.set(0L);
        cacheMisses.set(0L);
    }

    /**
     * 获取当前点级缓存统计快照。
     */
    public ElevationCacheStatistics statistics() {
        long requests = totalRequests.get();
        long hits = cacheHits.get();
        int size;
        synchronized (cache) {
            size = cache.size();
        }
        return new ElevationCacheStatistics(requests, hits, cacheMisses.get(), size, requests == 0L ? 0.0 : (double) hits / requests);
    }

    private void evictIfNecessary() {
        while (cache.size() > maximumCachedPoints) {
            var iterator = cache.entrySet().iterator();
            iterator.next();
            iterator.remove();
        }
    }

    private record CoordinateKey(long latitude, long longitude) {
        private static CoordinateKey from(GeoCoordinate coordinate, double scale) {
            return new CoordinateKey(Math.round(coordinate.latitude() * scale), Math.round(coordinate.longitude() * scale));
        }
    }

    private record CacheValue(boolean present, double elevationMetres) {
        private static CacheValue from(OptionalDouble elevation) {
            return elevation.isPresent() ? new CacheValue(true, elevation.getAsDouble()) : new CacheValue(false, 0.0);
        }

        private OptionalDouble toOptionalDouble() {
            return present ? OptionalDouble.of(elevationMetres) : OptionalDouble.empty();
        }
    }
}
