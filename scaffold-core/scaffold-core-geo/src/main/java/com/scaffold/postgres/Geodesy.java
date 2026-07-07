package com.scaffold.postgres;

/**
 * 地形采样和航路规划共用的球面地球测地计算工具。
 */
public final class Geodesy {

    /**
     * 地球平均半径，单位为米。
     */
    public static final double MEAN_EARTH_RADIUS_METRES = 6_371_000.0;

    private Geodesy() {
    }

    /**
     * 使用 Haversine 公式计算两个 WGS84 坐标之间的大圆距离。
     *
     * @param start 起点坐标
     * @param end   终点坐标
     * @return 两点间的大圆距离，单位为米
     */
    public static double distanceMetres(GeoCoordinate start, GeoCoordinate end) {
        double lat1 = Math.toRadians(start.latitude());
        double lat2 = Math.toRadians(end.latitude());
        double deltaLat = lat2 - lat1;
        double deltaLon = Math.toRadians(end.longitude() - start.longitude());
        double sinLat = Math.sin(deltaLat / 2.0);
        double sinLon = Math.sin(deltaLon / 2.0);
        double a = sinLat * sinLat + Math.cos(lat1) * Math.cos(lat2) * sinLon * sinLon;
        double clampedA = Math.max(0.0, Math.min(1.0, a));
        return MEAN_EARTH_RADIUS_METRES * 2.0 * Math.atan2(Math.sqrt(clampedA), Math.sqrt(1.0 - clampedA));
    }

    /**
     * 计算从起点沿大圆航线前往终点时的初始方位角。
     *
     * @param start 起点坐标
     * @param end   终点坐标
     * @return 初始方位角，单位为度；正北为 0°，顺时针递增，结果范围为 [0°, 360°)
     */
    public static double initialBearingDegrees(GeoCoordinate start, GeoCoordinate end) {
        double lat1 = Math.toRadians(start.latitude());
        double lat2 = Math.toRadians(end.latitude());
        double deltaLon = Math.toRadians(end.longitude() - start.longitude());
        double y = Math.sin(deltaLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltaLon);
        return normalizeBearing(Math.toDegrees(Math.atan2(y, x)));
    }

    /**
     * 根据起点、初始方位角和大圆距离计算终点坐标。
     *
     * @param start          起点坐标
     * @param bearingDegrees 初始方位角，单位为度；正北为 0°，顺时针递增
     * @param distanceMetres 沿地球表面的移动距离，单位为米，必须大于或等于 0
     * @return 计算得到的终点坐标，经度已归一化到 [-180°, 180°)
     */
    public static GeoCoordinate destination(GeoCoordinate start, double bearingDegrees, double distanceMetres) {
        if (!Double.isFinite(bearingDegrees)) {
            throw new IllegalArgumentException("bearingDegrees must be finite");
        }
        if (!Double.isFinite(distanceMetres) || distanceMetres < 0.0) {
            throw new IllegalArgumentException("distanceMetres must be finite and non-negative");
        }
        double angularDistance = distanceMetres / MEAN_EARTH_RADIUS_METRES;
        double bearing = Math.toRadians(bearingDegrees);
        double lat1 = Math.toRadians(start.latitude());
        double lon1 = Math.toRadians(start.longitude());
        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(angularDistance) + Math.cos(lat1) * Math.sin(angularDistance) * Math.cos(bearing));
        double lon2 = lon1 + Math.atan2(Math.sin(bearing) * Math.sin(angularDistance) * Math.cos(lat1), Math.cos(angularDistance) - Math.sin(lat1) * Math.sin(lat2));
        return new GeoCoordinate(Math.toDegrees(lat2), normalizeLongitude(Math.toDegrees(lon2)));
    }

    /**
     * 沿起点到终点的大圆航线进行插值。
     *
     * @param start    起点坐标
     * @param end      终点坐标
     * @param fraction 插值比例，范围为 [0, 1]；0 表示起点，1 表示终点
     * @return 大圆航线上对应比例位置的坐标
     */
    public static GeoCoordinate interpolate(GeoCoordinate start, GeoCoordinate end, double fraction) {
        if (!Double.isFinite(fraction) || fraction < 0.0 || fraction > 1.0) {
            throw new IllegalArgumentException("fraction must be between 0 and 1");
        }
        if (fraction == 0.0) return start;
        if (fraction == 1.0) return end;
        return destination(start, initialBearingDegrees(start, end), distanceMetres(start, end) * fraction);
    }

    /**
     * 将任意角度归一化到 [0°, 360°) 范围。
     */
    private static double normalizeBearing(double degrees) {
        return (degrees % 360.0 + 360.0) % 360.0;
    }

    /**
     * 将任意经度归一化到 [-180°, 180°) 范围。
     */
    private static double normalizeLongitude(double degrees) {
        return (degrees + 540.0) % 360.0 - 180.0;
    }
}
