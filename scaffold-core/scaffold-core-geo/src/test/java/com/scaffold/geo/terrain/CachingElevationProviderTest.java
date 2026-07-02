package com.scaffold.geo.terrain;

import com.scaffold.geo.GeoCoordinate;
import org.junit.jupiter.api.Test;

import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class CachingElevationProviderTest {

    @Test
    void cachesCoordinatesAtTheConfiguredPrecision() {
        AtomicInteger delegateCalls = new AtomicInteger();
        ElevationProvider delegate = coordinate -> {
            delegateCalls.incrementAndGet();
            return OptionalDouble.of(123.0);
        };
        CachingElevationProvider provider = new CachingElevationProvider(delegate, 10, 4, false);

        provider.findElevationMetres(new GeoCoordinate(39.900001, 116.400001));
        provider.findElevationMetres(new GeoCoordinate(39.900002, 116.400002));

        assertThat(delegateCalls).hasValue(1);
        assertThat(provider.statistics())
                .extracting(ElevationCacheStatistics::totalRequests,
                        ElevationCacheStatistics::cacheHits,
                        ElevationCacheStatistics::cacheMisses,
                        ElevationCacheStatistics::cachedPoints)
                .containsExactly(2L, 1L, 1L, 1);
        assertThat(provider.statistics().hitRate()).isEqualTo(0.5);
    }

    @Test
    void keepsThePointCacheBounded() {
        CachingElevationProvider provider = new CachingElevationProvider(
                coordinate -> OptionalDouble.of(coordinate.latitude()), 2, 6, false);

        provider.findElevationMetres(new GeoCoordinate(1.0, 1.0));
        provider.findElevationMetres(new GeoCoordinate(2.0, 2.0));
        provider.findElevationMetres(new GeoCoordinate(3.0, 3.0));

        assertThat(provider.statistics().cachedPoints()).isEqualTo(2);
    }

    @Test
    void doesNotCacheMissingElevationsByDefault() {
        AtomicInteger delegateCalls = new AtomicInteger();
        CachingElevationProvider provider = new CachingElevationProvider(coordinate -> {
            delegateCalls.incrementAndGet();
            return OptionalDouble.empty();
        }, 10, 6, false);
        GeoCoordinate coordinate = new GeoCoordinate(39.9, 116.4);

        provider.findElevationMetres(coordinate);
        provider.findElevationMetres(coordinate);

        assertThat(delegateCalls).hasValue(2);
        assertThat(provider.statistics().cachedPoints()).isZero();
    }
}
