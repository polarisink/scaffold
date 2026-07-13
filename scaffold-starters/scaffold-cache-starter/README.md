# scaffold-cache-starter

统一的 Spring Cache Starter，支持 Caffeine、Redis、PostgreSQL 单级缓存，以及 Caffeine + Redis/PostgreSQL 二级缓存。

```xml
<dependency>
    <groupId>com.scaffold</groupId>
    <artifactId>scaffold-cache-starter</artifactId>
</dependency>
```

## 单级缓存

默认使用 Caffeine：

```yaml
scaffold:
  cache:
    mode: single
    provider: caffeine # caffeine、redis、postgresql
    caffeine:
      spec: maximumSize=10000,expireAfterWrite=30m
    redis:
      time-to-live: 3d
      cache-null-values: false
      key-prefix: 'scaffold:cache:'
```

使用 Redis 时还需配置标准的 `spring.data.redis.*` 连接信息。

## 二级缓存

一级固定为 Caffeine，二级可选择 Redis 或 PostgreSQL：

```yaml
scaffold:
  cache:
    mode: two-level
    secondary: redis # redis、postgresql
```

读取顺序为 L1 -> L2，L2 命中后回填 L1；写入、删除和清空均先操作 L2，再操作 L1。

## PostgreSQL 缓存

Starter 会根据 `scaffold.cache.postgresql.datasource.*` 自动创建专用的 HikariCP 数据源和
`postgresqlJdbcTemplate`，无需在应用中声明数据源配置类。也可以自定义同名 Bean 或
`PostgresqlCacheStore` 覆盖默认实现。没有配置专用数据源时，回退使用系统中唯一或标记为
`@Primary` 的 `JdbcTemplate`。

所有数据源都会通过 JDBC 元数据校验是否为 PostgreSQL。如果系统数据源实际是 MySQL、Oracle
等数据库，应用会在启动时失败，此时应显式配置 PostgreSQL 缓存数据源：

PostgreSQL JDBC 驱动不会由 Starter 传递引入，使用方需要显式添加：

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

```yaml
scaffold:
  cache:
    mode: single
    provider: postgresql
    postgresql:
      datasource:
        url: jdbc:postgresql://localhost:5432/cache
        username: postgres
        password: postgres
        driver-class-name: org.postgresql.Driver
      table-name: scaffold_spring_cache
      default-ttl: 3d
      unlogged: true
      initialize-schema: true
      cleanup-on-startup: true
      scheduled-cleanup: true
      cleanup-interval: 5m
```

专用 PostgreSQL 数据源的优先级高于系统内置数据源。若选中 PostgreSQL 但没有可用
数据源/Store，应用会启动失败并给出明确提示。
