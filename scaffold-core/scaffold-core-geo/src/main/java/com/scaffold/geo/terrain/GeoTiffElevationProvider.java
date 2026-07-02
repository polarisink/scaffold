package com.scaffold.geo.terrain;

import com.scaffold.geo.GeoCoordinate;
import org.geotools.api.geometry.Position;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.Position2D;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;

/**
 * 线程安全且容量有界的 GeoTIFF DEM 读取器，使用 LRU 策略管理多个数据源。
 */
public final class GeoTiffElevationProvider implements ElevationProvider, AutoCloseable {

    private final DemSourceResolver sourceResolver;
    private final int maximumCachedSources;
    private final Map<URI, LoadedDem> cache = new LinkedHashMap<>(16, 0.75f, true);

    public GeoTiffElevationProvider(DemSourceResolver sourceResolver, int maximumCachedSources) {
        this.sourceResolver = Objects.requireNonNull(sourceResolver, "sourceResolver");
        if (maximumCachedSources < 1) {
            throw new IllegalArgumentException("maximumCachedSources must be at least 1");
        }
        this.maximumCachedSources = maximumCachedSources;
    }

    @Override
    public OptionalDouble findElevationMetres(GeoCoordinate coordinate) {
        DemSource source = sourceResolver.resolve(Objects.requireNonNull(coordinate, "coordinate")).orElse(null);
        if (source == null) return OptionalDouble.empty();
        URI sourceKey = source.identifier();
        LoadedDem dem;
        synchronized (cache) {
            dem = cache.get(sourceKey);
            if (dem == null) {
                try {
                    dem = LoadedDem.open(source);
                } catch (Exception ignored) {
                    return OptionalDouble.empty();
                }
                cache.put(sourceKey, dem);
                evictIfNecessary();
            }
        }
        return dem.evaluate(coordinate);
    }

    public int cachedSourceCount() {
        synchronized (cache) {
            return cache.size();
        }
    }

    public void clearCache() {
        synchronized (cache) {
            cache.values().forEach(LoadedDem::close);
            cache.clear();
        }
    }

    @Override
    public void close() {
        clearCache();
    }

    private void evictIfNecessary() {
        while (cache.size() > maximumCachedSources) {
            var iterator = cache.entrySet().iterator();
            var eldest = iterator.next();
            iterator.remove();
            eldest.getValue().close();
        }
    }

    private record LoadedDem(GeoTiffReader reader, GridCoverage2D coverage,
                             MathTransform wgs84ToCoverage) {

        private static LoadedDem open(DemSource source) throws Exception {
            GeoTiffReader reader = null;
            try {
                reader = new GeoTiffReader(source.localFile().toFile());
                GridCoverage2D coverage = reader.read();
                CoordinateReferenceSystem targetCrs = coverage.getCoordinateReferenceSystem2D();
                MathTransform transform = targetCrs == null
                        ? null
                        : CRS.findMathTransform(DefaultGeographicCRS.WGS84, targetCrs, true);
                return new LoadedDem(reader, coverage, transform);
            } catch (Exception exception) {
                if (reader != null) reader.dispose();
                throw exception;
            }
        }

        private OptionalDouble evaluate(GeoCoordinate coordinate) {
            try {
                Position source = new Position2D(
                        DefaultGeographicCRS.WGS84, coordinate.longitude(), coordinate.latitude());
                Position target = wgs84ToCoverage == null
                        ? source
                        : wgs84ToCoverage.transform(source, null);
                double[] values = new double[coverage.getNumSampleDimensions()];
                synchronized (coverage) {
                    coverage.evaluate(target, values);
                }
                double elevation = values[0];
                return Double.isFinite(elevation) ? OptionalDouble.of(elevation) : OptionalDouble.empty();
            } catch (Exception ignored) {
                return OptionalDouble.empty();
            }
        }

        private void close() {
            try {
                coverage.dispose(true);
            } finally {
                reader.dispose();
            }
        }
    }
}
