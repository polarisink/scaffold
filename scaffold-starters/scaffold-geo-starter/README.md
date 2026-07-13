# scaffold-starter-geo

`scaffold-core-geo` 的 Spring Boot 自动配置模块。引入后无需手动创建 `ResourceLoader`、区域索引或
GeoTIFF 读取器，应用直接注入 `ElevationProvider` 即可。

## 配置

```yaml
scaffold:
  geo:
    enabled: true
    province-boundary-location: classpath:/scaffold/geo/province-boundaries.csv
    dem-base-location: file:./dem/ # 也支持 classpath:/dem/
    dem-file-name-pattern: "{id-lower}.tif"
    maximum-cached-sources: 5
    point-cache-enabled: true
    maximum-cached-points: 100000
    coordinate-decimal-places: 6
    cache-missing-elevations: false
```

模块内置省级边界数据。自定义边界 CSV 的字段顺序为：

```text
区域代码,区域名称,最小经度,最小纬度,最大经度,最大纬度
```

DEM 文件名模板支持 `{id}`、`{id-lower}` 和 `{name}` 占位符。

## 高程查询

```java
@Service
public class TerrainService {
    private final ElevationProvider elevations;

    public TerrainService(ElevationProvider elevations) {
        this.elevations = elevations;
    }

    public double getElevation(double latitude, double longitude) {
        return elevations.elevationMetresOrDefault(
                new GeoCoordinate(latitude, longitude), 0.0);
    }
}
```

GeoTIFF 查询失败返回 `OptionalDouble.empty()`，由调用方明确决定回退策略。

## 地形通视

```java
LineOfSightResult result = TerrainAnalysis.lineOfSight(
        elevations,
        new GeoPosition(39.9, 116.4, 120.0),
        new GeoPosition(40.0, 116.8, 500.0),
        100.0,
        4.0 / 3.0,
        10.0);
```

## 运行统计

```java
TerrainStatistics statistics = terrainMetrics.snapshot();
double hitRate = statistics.elevationCache().hitRate();
int loadedDemSources = statistics.loadedDemSources();
```
