# scaffold-core-geo

通用地理与地形内核，不依赖 Spring。

主要能力：

- WGS84 坐标、距离、方位角和大圆航线计算；
- 区域边界索引；
- GeoTIFF DEM 高程读取与数据源 LRU 缓存；
- 有界的点级高程缓存及统计；
- 地形剖面、通视、遮挡点、复杂度和山脊分析。

模块内所有双坐标 API 均使用“纬度、经度”语义，并优先接收 `GeoCoordinate`。

Spring Boot 应用通常不直接依赖本模块，请使用 `scaffold-starter-geo`。
