package com.scaffold.geo.dto;

import java.time.OffsetDateTime;

public record MapPlaceResponse(
        Long id,
        String name,
        double lon,
        double lat,
        Double distanceMeters,
        String geoJson,
        OffsetDateTime createdAt
) {
}
