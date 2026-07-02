package com.scaffold.geo;

/**
 * WGS84 坐标。本模块的 API 始终先传纬度，再传经度。
 */
public record GeoCoordinate(double latitude, double longitude) {

    public GeoCoordinate {
        if (!Double.isFinite(latitude) || latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("latitude must be finite and between -90 and 90 degrees");
        }
        if (!Double.isFinite(longitude) || longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("longitude must be finite and between -180 and 180 degrees");
        }
    }
}
