package com.scaffold.postgres.terrain;

import com.scaffold.postgres.GeoCoordinate;

import java.util.Optional;

@FunctionalInterface
public interface DemSourceResolver {

    Optional<DemSource> resolve(GeoCoordinate coordinate);
}
