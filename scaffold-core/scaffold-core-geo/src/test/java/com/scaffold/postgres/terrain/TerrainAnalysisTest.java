package com.scaffold.postgres.terrain;

import com.scaffold.postgres.GeoCoordinate;
import com.scaffold.postgres.GeoPosition;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.OptionalDouble;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class TerrainAnalysisTest {

    @Test
    void detectsTerrainObstructionAndReturnsItsLocation() {
        GeoPosition observer = new GeoPosition(0.0, 0.0, 100.0);
        GeoPosition target = new GeoPosition(0.0, 0.01, 100.0);
        ElevationProvider hill = coordinate -> coordinate.longitude() > 0.004
                && coordinate.longitude() < 0.006 ? OptionalDouble.of(150.0) : OptionalDouble.of(0.0);

        LineOfSightResult result = TerrainAnalysis.lineOfSight(
                hill, observer, target, 100.0, 1.0, 0.0);

        assertThat(result.visible()).isFalse();
        assertThat(result.firstObstruction()).isPresent().get()
                .extracting(TerrainProfilePoint::terrainElevationMetres).isEqualTo(150.0);
    }

    @Test
    void calculatesComplexityAndActualLocalRidges() {
        GeoCoordinate coordinate = new GeoCoordinate(0.0, 0.0);
        List<TerrainProfilePoint> profile = List.of(
                point(coordinate, 0.0, 1.0), point(coordinate, 1.0, 4.0),
                point(coordinate, 2.0, 2.0), point(coordinate, 3.0, 5.0),
                point(coordinate, 4.0, 3.0));

        assertThat(TerrainAnalysis.ridgePoints(profile))
                .extracting(TerrainProfilePoint::terrainElevationMetres)
                .containsExactly(4.0, 5.0);
        assertThat(TerrainAnalysis.elevationStandardDeviation(profile))
                .isCloseTo(Math.sqrt(2.0), offset(1e-10));
    }

    private static TerrainProfilePoint point(GeoCoordinate coordinate, double distance, double elevation) {
        return new TerrainProfilePoint(coordinate, distance, elevation, 100.0, true);
    }
}
