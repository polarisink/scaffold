package com.scaffold.geo.controller;

import com.scaffold.base.util.R;
import com.scaffold.geo.dto.CreatePlaceRequest;
import com.scaffold.geo.dto.MapPlaceResponse;
import com.scaffold.geo.dto.MapRegionResponse;
import com.scaffold.geo.service.MapQueryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/maps")
public class MapQueryController {

    private final MapQueryService mapQueryService;

    public MapQueryController(MapQueryService mapQueryService) {
        this.mapQueryService = mapQueryService;
    }

    @PostMapping("/places")
    public R<MapPlaceResponse> createPlace(@Valid @RequestBody CreatePlaceRequest request) {
        return R.success(mapQueryService.createPlace(request));
    }

    @GetMapping("/places/nearby")
    public R<List<MapPlaceResponse>> findNearbyPlaces(
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") double lon,
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") double lat,
            @RequestParam(defaultValue = "1000") @Min(1) @Max(100000) double radiusMeters) {
        return R.success(mapQueryService.findNearbyPlaces(lon, lat, radiusMeters));
    }

    @GetMapping("/places/{id}")
    public R<MapPlaceResponse> findPlace(@PathVariable Long id) {
        return R.success(mapQueryService.findPlace(id));
    }

    @GetMapping("/regions/contains")
    public R<List<MapRegionResponse>> findRegionsContainingPoint(
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") double lon,
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") double lat) {
        return R.success(mapQueryService.findRegionsContainingPoint(lon, lat));
    }

    @GetMapping("/regions/intersects")
    public R<List<MapRegionResponse>> findRegionsInViewport(
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") double minLon,
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") double minLat,
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") double maxLon,
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") double maxLat) {
        return R.success(mapQueryService.findRegionsInViewport(minLon, minLat, maxLon, maxLat));
    }
}
