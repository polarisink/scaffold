# PostgreSQL PostGIS Geo Demo

这个模块演示 Spring Boot 使用 PostgreSQL + PostGIS 做地图查询，REST 层到最底层 Geo SQL 都在模块内。

## 准备数据库

```sql
CREATE DATABASE scaffold_geo;
```

应用启动时会执行 `schema.sql` 和 `data.sql`：

- `CREATE EXTENSION IF NOT EXISTS postgis`
- 创建 `map_place` 点位表
- 创建 `map_region` 区域围栏表
- 创建 GIST 空间索引
- 写入几条上海示例数据

如果当前数据库用户没有创建扩展权限，可以先用管理员账号执行：

```sql
CREATE EXTENSION IF NOT EXISTS postgis;
```

## 启动

默认连接：

```yaml
spring.datasource.url: jdbc:postgresql://localhost:5432/scaffold_geo
spring.datasource.username: postgres
spring.datasource.password: postgres
```

启动：

```bash
./mvnw -pl scaffold-test/scaffold-test-postgresql-geo -am spring-boot:run -Pexamples
```

## 接口示例

附近点位查询，底层使用 `ST_DWithin` + `ST_Distance`：

```bash
curl 'http://localhost:8096/api/maps/places/nearby?lon=121.47&lat=31.23&radiusMeters=5000'
```

查询点位详情，返回 GeoJSON：

```bash
curl 'http://localhost:8096/api/maps/places/1'
```

新增点位，底层使用 `ST_SetSRID(ST_MakePoint(lon, lat), 4326)`：

```bash
curl -X POST 'http://localhost:8096/api/maps/places' \
  -H 'Content-Type: application/json' \
  -d '{"name":"测试点位","lon":121.48,"lat":31.22}'
```

点落在哪些区域内，底层使用 `ST_Contains`：

```bash
curl 'http://localhost:8096/api/maps/regions/contains?lon=121.47&lat=31.23'
```

地图视窗相交区域，底层使用 `ST_MakeEnvelope` + `ST_Intersects`：

```bash
curl 'http://localhost:8096/api/maps/regions/intersects?minLon=121.35&minLat=31.10&maxLon=121.55&maxLat=31.30'
```

## 代码路径

- `MapQueryController`：REST 接口层
- `MapQueryService`：业务层和参数规则
- `MapGeoRepository`：`JdbcTemplate` + PostGIS 原生 SQL
- `schema.sql` / `data.sql`：PostGIS 表结构、索引、示例数据
