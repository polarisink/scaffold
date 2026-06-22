package com.scaffold.geo.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePlaceRequest(
        @NotBlank
        @Size(max = 80)
        String name,

        @DecimalMin("-180.0")
        @DecimalMax("180.0")
        double lon,

        @DecimalMin("-90.0")
        @DecimalMax("90.0")
        double lat
) {
}
