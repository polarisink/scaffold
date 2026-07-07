package com.scaffold.postgres.service;

import com.scaffold.postgres.dto.CreatePlaceRequest;
import com.scaffold.postgres.dto.MapPlaceResponse;
import com.scaffold.postgres.dto.MapRegionResponse;
import com.scaffold.postgres.repository.MapGeoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MapQueryService {

    private final MapGeoRepository mapGeoRepository;

    public MapQueryService(MapGeoRepository mapGeoRepository) {
        this.mapGeoRepository = mapGeoRepository;
    }

    @Transactional
    public MapPlaceResponse createPlace(CreatePlaceRequest request) {
        return mapGeoRepository.insertPlace(request);
    }

    public MapPlaceResponse findPlace(Long id) {
        return mapGeoRepository.findPlace(id)
                .orElseThrow(() -> new IllegalArgumentException("地图点位不存在: " + id));
    }

    public List<MapPlaceResponse> findNearbyPlaces(double lon, double lat, double radiusMeters) {
        return mapGeoRepository.findNearbyPlaces(lon, lat, radiusMeters);
    }

    public List<MapRegionResponse> findRegionsContainingPoint(double lon, double lat) {
        return mapGeoRepository.findRegionsContainingPoint(lon, lat);
    }

    public List<MapRegionResponse> findRegionsInViewport(double minLon, double minLat, double maxLon, double maxLat) {
        if (minLon >= maxLon || minLat >= maxLat) {
            throw new IllegalArgumentException("地图视窗参数必须满足 minLon < maxLon 且 minLat < maxLat");
        }
        return mapGeoRepository.findRegionsInViewport(minLon, minLat, maxLon, maxLat);
    }
}
