package com.scaffold.geo.repository;

import com.scaffold.base.util.JsonUtil;
import com.scaffold.geo.dto.CreatePlaceRequest;
import com.scaffold.geo.dto.MapPlaceResponse;
import com.scaffold.geo.dto.MapRegionResponse;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Repository
public class MapGeoRepository {

    private static final RowMapper<MapPlaceResponse> PLACE_ROW_MAPPER = (rs, rowNum) -> {
        Timestamp createdAt = rs.getTimestamp("created_at");
        OffsetDateTime createdAtUtc = createdAt == null ? null : createdAt.toInstant().atOffset(ZoneOffset.UTC);
        Double distanceMeters = null;
        try {
            distanceMeters = rs.getObject("distance_meters", Double.class);
        } catch (Exception ignored) {
            // Some queries intentionally do not project distance_meters.
        }
        return new MapPlaceResponse(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getDouble("lon"),
                rs.getDouble("lat"),
                distanceMeters,
                JsonUtil.readTree(rs.getString("geojson")),
                createdAtUtc
        );
    };

    private static final RowMapper<MapRegionResponse> REGION_ROW_MAPPER = (rs, rowNum) -> new MapRegionResponse(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("geojson")
    );

    private final JdbcTemplate jdbcTemplate;

    public MapGeoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public MapPlaceResponse insertPlace(CreatePlaceRequest request) {
        String sql = """
                INSERT INTO map_place (name, location)
                VALUES (?, ST_SetSRID(ST_MakePoint(?, ?), 4326))
                RETURNING id
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, request.name());
            ps.setDouble(2, request.lon());
            ps.setDouble(3, request.lat());
            return ps;
        }, keyHolder);
        Number id = keyHolder.getKey();
        if (id == null) {
            throw new IllegalStateException("create place failed: database did not return id");
        }
        return findPlace(id.longValue()).orElseThrow();
    }

    public Optional<MapPlaceResponse> findPlace(Long id) {
        String sql = """
                SELECT id,
                       name,
                       ST_X(location) AS lon,
                       ST_Y(location) AS lat,
                       NULL::double precision AS distance_meters,
                       ST_AsGeoJSON(location) AS geojson,
                       created_at
                FROM map_place
                WHERE id = ?
                """;
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, PLACE_ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<MapPlaceResponse> findNearbyPlaces(double lon, double lat, double radiusMeters) {
        String sql = """
                WITH center AS (
                    SELECT ST_SetSRID(ST_MakePoint(?, ?), 4326) AS geom
                )
                SELECT p.id,
                       p.name,
                       ST_X(p.location) AS lon,
                       ST_Y(p.location) AS lat,
                       ST_Distance(p.location::geography, center.geom::geography) AS distance_meters,
                       ST_AsGeoJSON(p.location) AS geojson,
                       p.created_at
                FROM map_place p
                CROSS JOIN center
                WHERE p.location && ST_Expand(center.geom, ? / 111320.0)
                  AND ST_DWithin(p.location::geography, center.geom::geography, ?)
                ORDER BY distance_meters ASC
                """;
        return jdbcTemplate.query(sql, PLACE_ROW_MAPPER, lon, lat, radiusMeters, radiusMeters);
    }

    public List<MapRegionResponse> findRegionsContainingPoint(double lon, double lat) {
        String sql = """
                WITH point AS (
                    SELECT ST_SetSRID(ST_MakePoint(?, ?), 4326) AS geom
                )
                SELECT r.id,
                       r.name,
                       ST_AsGeoJSON(r.boundary) AS geojson
                FROM map_region r
                CROSS JOIN point
                WHERE r.boundary && point.geom
                  AND ST_Contains(r.boundary, point.geom)
                ORDER BY r.id
                """;
        return jdbcTemplate.query(sql, REGION_ROW_MAPPER, lon, lat);
    }

    public List<MapRegionResponse> findRegionsInViewport(double minLon, double minLat, double maxLon, double maxLat) {
        String sql = """
                WITH viewport AS (
                    SELECT ST_MakeEnvelope(?, ?, ?, ?, 4326) AS geom
                )
                SELECT r.id,
                       r.name,
                       ST_AsGeoJSON(ST_Intersection(r.boundary, viewport.geom)) AS geojson
                FROM map_region r
                CROSS JOIN viewport
                WHERE r.boundary && viewport.geom
                  AND ST_Intersects(r.boundary, viewport.geom)
                ORDER BY r.id
                """;
        return jdbcTemplate.query(sql, REGION_ROW_MAPPER, minLon, minLat, maxLon, maxLat);
    }
}
