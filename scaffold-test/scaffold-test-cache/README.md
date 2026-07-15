# PostgreSQL 缓存示例

演示 `scaffold-cache-starter` 使用 PostgreSQL 作为 Spring Cache 存储。`GET /cache` 使用固定缓存键；连续访问时 `invocation` 和 `createdAt` 不变，说明结果来自缓存。

## 前置条件

准备 PostgreSQL 数据库，并修改 `application.yml` 中两处数据源配置。默认示例为 `localhost:5432/aries`、用户 `aries`。Starter 会自动创建 `scaffold_spring_cache` 表，缓存默认有效期为 3 天，并每 5 分钟清理过期记录。

## 运行与验证

```bash
./mvnw -pl scaffold-test/scaffold-test-cache -am -Pexamples spring-boot:run
curl http://localhost:8096/cache
curl http://localhost:8096/cache
```

如需验证缓存失效，可删除表中 `cache_name = 'user'` 的记录后再次请求。该模块和 `scaffold-test-postgresql-geo` 默认都使用 `8096`，不要同时启动。
