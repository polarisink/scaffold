# PostgreSQL Cache Starter

Spring Boot 3 自动装配版 PostgreSQL Cache，底层实现来自 `scaffold-core-postgresql-cache`。

## 依赖

```xml
<dependency>
    <groupId>com.scaffold</groupId>
    <artifactId>scaffold-starter-postgresql-cache</artifactId>
</dependency>
```

## 能力

- `caffeine`：本地缓存
- `postgresql`：PostgreSQL 表缓存
- `two-level`：Caffeine + PostgreSQL 两级缓存
- `two-level` 模式使用 PostgreSQL `LISTEN/NOTIFY` 广播 L1 缓存失效

## 配置

```yaml
scaffold:
  cache:
    mode: two-level # caffeine | postgresql | two-level
    caffeine:
      maximum-size: 10000
      expire-after-write: 10m
    postgresql:
      table-name: scaffold_spring_cache
      default-ttl: 3d
      unlogged: true
      initialize-schema: true
      cleanup-on-startup: true
      scheduled-cleanup: true
      cleanup-interval: 5m
    cluster-invalidation:
      enabled: true
      channel: scaffold_cache_invalidate
      poll-interval: 2s
```

只依赖 `scaffold-core-postgresql-cache` 不会触发自动装配；需要开箱即用时依赖本 starter。
