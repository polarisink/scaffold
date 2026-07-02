package com.scaffold.geo.terrain;

import com.scaffold.geo.GeoCoordinate;

import java.util.Optional;

@FunctionalInterface
public interface DemSourceResolver {

    Optional<DemSource> resolve(GeoCoordinate coordinate);
}
