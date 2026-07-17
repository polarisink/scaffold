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
    type: caffeine # caffeine、redis、postgresql
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
`postgresqlJdbcTemplate`，无需在应用中声明数据源配置类。PostgreSQL 缓存始终使用该专用
数据源，不会复用 `spring.datasource` 或系统中的 `JdbcTemplate`。因此业务数据库可以是
MySQL、Oracle 或其他数据库，也可以排除 `DataSourceAutoConfiguration`。

应用也可以提供名为 `postgresqlCacheDataSource` 的自定义数据源 Bean，或直接提供
`PostgresqlCacheStore` 覆盖默认实现。选中 PostgreSQL 缓存但没有提供专用数据源或 Store 时，
应用会启动失败并给出明确提示。

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
    type: postgresql
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

`postgresqlCacheDataSource` 和 `postgresqlJdbcTemplate` 均为非默认候选，不会参与 JPA、
MyBatis 等业务组件的默认数据源选择。
