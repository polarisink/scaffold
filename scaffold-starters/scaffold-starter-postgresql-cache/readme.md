# PostgreSQL Cache Starter

该 starter 提供 PostgreSQL Cache 的配置属性、`PostgresqlCacheStore`、`PostgresqlCacheManager`
和过期数据清理基础设施。引入 starter 后会自动启用 Spring Cache，并在没有其它 `CacheManager`
时自动创建 PostgreSQL 缓存管理器。
缓存值使用 Jackson 序列化；如果容器中存在 `redisObjectMapper`，会优先复用该 ObjectMapper 的缓存类型信息配置。

## Caffeine

确保项目中存在 Caffeine 依赖，然后通过配置选择：

```yaml
spring:
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=10000,expireAfterWrite=10m
```

## Redis

确保项目中存在 Redis 连接工厂（项目中的 `scaffold-core-redis` 已提供相关依赖），然后配置：

```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 3d
```

## PostgreSQL

引入 starter：

```xml

<dependency>
    <groupId>com.scaffold</groupId>
    <artifactId>scaffold-starter-postgresql-cache</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

配置 PostgreSQL 缓存参数：

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

此时不需要设置 `spring.cache.type`，也不需要手动声明 `PostgresqlCacheManager`。
如果应用已经声明了其它 `CacheManager`，starter 会自动退让。

starter 会校验 classpath 中存在 `org.postgresql.Driver` 后才装配 PostgreSQL cache 相关 Bean，
并在创建 `PostgresqlCacheStore` 时通过 JDBC 元数据确认当前 `JdbcTemplate` 连接的是 PostgreSQL。
当容器中存在多个 `JdbcTemplate` 且没有名为 `postgresqlJdbcTemplate` 的 Bean 时，starter 会启动失败并提示显式指定
PostgreSQL 缓存使用的 `JdbcTemplate`。

如果应用同时配置 MySQL 和 PostgreSQL 数据源，且 MySQL 是主数据源，可为 PostgreSQL 缓存数据源声明名为
`postgresqlJdbcTemplate` 的 Bean，starter 会优先使用它创建 `PostgresqlCacheStore`：

```java

@Configuration(proxyBeanMethods = false)
class PostgresqlCacheDataSourceConfiguration {

    @Bean
    JdbcTemplate postgresqlJdbcTemplate(@Qualifier("postgresqlDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
```

也可以直接声明 `PostgresqlCacheStore` 或 `CacheManager`，starter 会自动退让：

```java

@Bean
PostgresqlCacheStore postgresqlCacheStore(@Qualifier("postgresqlJdbcTemplate") JdbcTemplate jdbcTemplate) {
    return new PostgresqlCacheStore(jdbcTemplate);
}
```
