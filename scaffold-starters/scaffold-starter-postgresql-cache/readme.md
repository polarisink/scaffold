# PostgreSQL Cache Starter

该 starter 提供 PostgreSQL Cache 的配置属性、`PostgresqlCacheStore` 和过期数据清理基础设施，
但不会自动创建 `CacheManager`。因此 Caffeine 和 Redis 仍可由 Spring Boot 的
`spring.cache.type` 正常选择。

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

由应用显式创建缓存管理器：

```java

@Configuration(proxyBeanMethods = false)
@EnableCaching
class CacheConfiguration {

    @Bean
    PostgresqlCacheManager cacheManager(PostgresqlCacheStore cacheStore,
                                        PostgresqlCacheProperties properties) {
        return new PostgresqlCacheManager(cacheStore, properties);
    }
}
```

Bean 方法应返回具体的 `PostgresqlCacheManager` 类型，以便 starter 仅在 PostgreSQL 缓存启用时启动定时清理。
此时不需要设置 `spring.cache.type`；用户声明的 `CacheManager` 会使 Spring Boot 自动配置退让。
