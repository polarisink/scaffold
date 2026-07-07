package com.scaffold.postgres.terrain;

import java.util.List;
import java.util.Optional;

public record LineOfSightResult(boolean visible, List<TerrainProfilePoint> profile) {

    public LineOfSightResult {
        profile = List.copyOf(profile);
    }

    public Optional<TerrainProfilePoint> firstObstruction() {
        return profile.stream().filter(point -> !point.visible()).findFirst();
    }
}
