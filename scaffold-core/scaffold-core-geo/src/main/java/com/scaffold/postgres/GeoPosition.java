package com.scaffold.postgres;

/**
 * 带海拔高度的 WGS84 坐标，高度单位为米，基准为平均海平面。
 */
public record GeoPosition(double latitude, double longitude, double altitudeMetres) {

    public GeoPosition {
        new GeoCoordinate(latitude, longitude);
        if (!Double.isFinite(altitudeMetres)) {
            throw new IllegalArgumentException("altitudeMetres must be finite");
        }
    }

    public GeoCoordinate coordinate() {
        return new GeoCoordinate(latitude, longitude);
    }
}
