package com.scaffold.postgres.dto;

public record MapRegionResponse(
        Long id,
        String name,
        String geoJson
) {
}
