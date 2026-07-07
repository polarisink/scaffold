package com.scaffold.postgres;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeodesyTest {

    @Test
    void calculatesDistanceBearingAndDestination() {
        GeoCoordinate origin = new GeoCoordinate(0.0, 0.0);
        GeoCoordinate east = new GeoCoordinate(0.0, 1.0);

        assertThat(Geodesy.distanceMetres(origin, east)).isCloseTo(111_194.9, within(1.0));
        assertThat(Geodesy.initialBearingDegrees(origin, east)).isCloseTo(90.0, within(1e-10));
        assertThat(Geodesy.destination(origin, 90.0, Geodesy.distanceMetres(origin, east)))
                .satisfies(point -> {
                    assertThat(point.latitude()).isCloseTo(0.0, within(1e-10));
                    assertThat(point.longitude()).isCloseTo(1.0, within(1e-10));
                });
    }

    @Test
    void validatesCoordinateOrderAndRanges() {
        assertThatThrownBy(() -> new GeoCoordinate(116.4, 39.9))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("latitude");
    }

    private static org.assertj.core.data.Offset<Double> within(double value) {
        return org.assertj.core.data.Offset.offset(value);
    }
}
