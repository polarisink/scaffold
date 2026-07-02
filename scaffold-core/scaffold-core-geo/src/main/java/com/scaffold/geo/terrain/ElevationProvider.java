package com.scaffold.geo.terrain;

import com.scaffold.geo.GeoCoordinate;

import java.util.OptionalDouble;

@FunctionalInterface
public interface ElevationProvider {

    OptionalDouble findElevationMetres(GeoCoordinate coordinate);

    default double elevationMetresOrDefault(GeoCoordinate coordinate, double fallback) {
        return findElevationMetres(coordinate).orElse(fallback);
    }
}
