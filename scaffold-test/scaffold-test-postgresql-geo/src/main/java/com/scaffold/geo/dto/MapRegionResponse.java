package com.scaffold.geo.dto;

public record MapRegionResponse(
        Long id,
        String name,
        String geoJson
) {
}
