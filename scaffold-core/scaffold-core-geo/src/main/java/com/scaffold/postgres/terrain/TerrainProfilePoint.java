package com.scaffold.postgres.terrain;

import com.scaffold.postgres.GeoCoordinate;

/**
 * 地形剖面中的单个采样点。
 *
 * @param coordinate              地形采样点的 WGS84 坐标
 * @param distanceMetres          采样点距剖面起点的地表距离，单位为米
 * @param terrainElevationMetres  采样点的地形高程，单位为米，基准为平均海平面
 * @param lineOfSightHeightMetres 考虑地球曲率后，采样点处通视线的高度，单位为米，基准为平均海平面
 * @param visible                 地形高程加净空要求是否不高于通视线；为 {@code true} 表示该采样点未造成遮挡
 */
public record TerrainProfilePoint(GeoCoordinate coordinate, double distanceMetres, double terrainElevationMetres,
                                  double lineOfSightHeightMetres, boolean visible) {
}
