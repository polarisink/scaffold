package com.scaffold.postgres.terrain;

import com.scaffold.postgres.GeoCoordinate;

import java.util.OptionalDouble;

@FunctionalInterface
public interface ElevationProvider {

    OptionalDouble findElevationMetres(GeoCoordinate coordinate);

    default double elevationMetresOrDefault(GeoCoordinate coordinate, double fallback) {
        return findElevationMetres(coordinate).orElse(fallback);
    }
}
