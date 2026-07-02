package com.scaffold.geo.region;

import com.scaffold.geo.GeoCoordinate;

/**
 * 区域
 *
 * @param id           id
 * @param name         名字
 * @param minLongitude 最小经度
 * @param minLatitude  最小纬度
 * @param maxLongitude 最大经度
 * @param maxLatitude  最大纬度
 */
public record GeoRegion(String id, String name, double minLongitude, double minLatitude, double maxLongitude,
                        double maxLatitude) {

    /**
     * 区域构造校验
     *
     * @param id           id
     * @param name         名字
     * @param minLongitude 最小经度
     * @param minLatitude  最小纬度
     * @param maxLongitude 最大经度
     * @param maxLatitude  最大纬度
     */
    public GeoRegion {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id must not be blank");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name must not be blank");
        new GeoCoordinate(minLatitude, minLongitude);
        new GeoCoordinate(maxLatitude, maxLongitude);
        if (minLatitude > maxLatitude || minLongitude > maxLongitude) {
            throw new IllegalArgumentException("minimum bounds must not exceed maximum bounds");
        }
    }

    /**
     * 区域是否包含某个点
     *
     * @param coordinate 点
     * @return 是否包含
     */
    public boolean contains(GeoCoordinate coordinate) {
        return coordinate.longitude() >= minLongitude && coordinate.longitude() <= maxLongitude && coordinate.latitude() >= minLatitude && coordinate.latitude() <= maxLatitude;
    }
}
