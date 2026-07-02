package com.scaffold.geo.terrain;

import com.scaffold.geo.GeoCoordinate;
import com.scaffold.geo.GeoPosition;
import com.scaffold.geo.Geodesy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class TerrainAnalysis {

    private TerrainAnalysis() {
    }

    /**
     * 对观察点与目标点之间的开区间进行采样，高度均以平均海平面为基准。
     * 有效地球半径系数通常取 1.0（几何模型）或 4/3（标准大气模型）。
     */
    public static LineOfSightResult lineOfSight(ElevationProvider elevations,
                                                GeoPosition observer, GeoPosition target,
                                                double sampleSpacingMetres,
                                                double effectiveEarthRadiusFactor,
                                                double clearanceMetres) {
        Objects.requireNonNull(elevations, "elevations");
        Objects.requireNonNull(observer, "observer");
        Objects.requireNonNull(target, "target");
        requirePositiveFinite(sampleSpacingMetres, "sampleSpacingMetres");
        requirePositiveFinite(effectiveEarthRadiusFactor, "effectiveEarthRadiusFactor");
        if (!Double.isFinite(clearanceMetres) || clearanceMetres < 0.0) {
            throw new IllegalArgumentException("clearanceMetres must be finite and non-negative");
        }

        GeoCoordinate start = observer.coordinate();
        GeoCoordinate end = target.coordinate();
        double totalDistance = Geodesy.distanceMetres(start, end);
        if (totalDistance == 0.0) return new LineOfSightResult(true, List.of());

        int segmentCount = Math.max(1, (int) Math.ceil(totalDistance / sampleSpacingMetres));
        double effectiveRadius = Geodesy.MEAN_EARTH_RADIUS_METRES * effectiveEarthRadiusFactor;
        List<TerrainProfilePoint> profile = new ArrayList<>(Math.max(0, segmentCount - 1));
        boolean allVisible = true;

        for (int segment = 1; segment < segmentCount; segment++) {
            double fraction = (double) segment / segmentCount;
            double distance = totalDistance * fraction;
            GeoCoordinate coordinate = Geodesy.interpolate(start, end, fraction);
            double terrain = elevations.elevationMetresOrDefault(coordinate, 0.0);
            double linearHeight = observer.altitudeMetres()
                    + (target.altitudeMetres() - observer.altitudeMetres()) * fraction;
            double earthBulge = distance * (totalDistance - distance) / (2.0 * effectiveRadius);
            double lineHeight = linearHeight - earthBulge;
            boolean visible = terrain + clearanceMetres <= lineHeight;
            allVisible &= visible;
            profile.add(new TerrainProfilePoint(coordinate, distance, terrain, lineHeight, visible));
        }
        return new LineOfSightResult(allVisible, profile);
    }

    public static double elevationStandardDeviation(List<TerrainProfilePoint> profile) {
        Objects.requireNonNull(profile, "profile");
        if (profile.isEmpty()) return 0.0;
        double mean = profile.stream().mapToDouble(TerrainProfilePoint::terrainElevationMetres)
                .average().orElse(0.0);
        double variance = profile.stream()
                .mapToDouble(point -> {
                    double difference = point.terrainElevationMetres() - mean;
                    return difference * difference;
                })
                .average().orElse(0.0);
        return Math.sqrt(variance);
    }

    /**
     * 从已采样的地形剖面中返回严格的局部高程极大值点。
     */
    public static List<TerrainProfilePoint> ridgePoints(List<TerrainProfilePoint> profile) {
        Objects.requireNonNull(profile, "profile");
        if (profile.size() < 3) return List.of();
        List<TerrainProfilePoint> ridges = new ArrayList<>();
        for (int index = 1; index < profile.size() - 1; index++) {
            TerrainProfilePoint previous = profile.get(index - 1);
            TerrainProfilePoint current = profile.get(index);
            TerrainProfilePoint next = profile.get(index + 1);
            if (current.terrainElevationMetres() > previous.terrainElevationMetres()
                    && current.terrainElevationMetres() > next.terrainElevationMetres()) {
                ridges.add(current);
            }
        }
        return List.copyOf(ridges);
    }

    private static void requirePositiveFinite(double value, String name) {
        if (!Double.isFinite(value) || value <= 0.0) {
            throw new IllegalArgumentException(name + " must be finite and positive");
        }
    }
}
