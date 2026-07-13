# scaffold-postgresql-cache-starter

将 PostgreSQL 表作为 Spring Cache 后端，适合希望避免额外部署 Redis 的中小型应用。

```xml
<dependency>
    <groupId>com.scaffold</groupId>
    <artifactId>scaffold-postgresql-cache-starter</artifactId>
</dependency>
```

```yaml
scaffold:
  cache:
    postgresql:
      table-name: scaffold_spring_cache
      default-ttl: 3d
      unlogged: true
      initialize-schema: true
      cleanup-on-startup: true
      scheduled-cleanup: true
      cleanup-interval: 5m
```

不需要设置 `spring.cache.type`。存在 PostgreSQL 驱动、连接确实指向 PostgreSQL且没有其他 `CacheManager` 时，Starter 才会自动创建缓存管理器。

缓存值使用 Jackson 序列化。如果容器中有多个 `JdbcTemplate`，请提供名为 `postgresqlJdbcTemplate` 的 Bean；也可自行声明 `PostgresqlCacheStore` 或 `CacheManager` 覆盖自动配置。

本方案适合普通键值缓存，不适合高吞吐热点缓存。Caffeine 和 Redis 应使用 Spring Boot 标准配置。
