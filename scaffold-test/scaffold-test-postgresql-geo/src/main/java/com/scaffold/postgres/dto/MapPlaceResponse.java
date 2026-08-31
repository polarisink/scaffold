package com.scaffold.postgres.dto;

import tools.jackson.databind.JsonNode;

import java.time.OffsetDateTime;

public record MapPlaceResponse(
        Long id,
        String name,
        double lon,
        double lat,
        Double distanceMeters,
        JsonNode geoJson,
        OffsetDateTime createdAt
) {
}
