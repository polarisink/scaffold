# scaffold-core-postgresql-cache

基于 PostgreSQL 表实现的 Spring Cache 内核，不提供自动配置。业务应用通常应依赖
[`scaffold-postgresql-cache-starter`](../../scaffold-starters/scaffold-postgresql-cache-starter/README.md)。

## 主要能力

- `PostgresqlCacheStore`：通过 `JdbcTemplate` 读写缓存记录。
- `PostgresqlCacheManager`、`PostgresqlCache`：Spring Cache 接口实现。
- Jackson 值序列化、过期记录清理、TTL 和 PostgreSQL `UNLOGGED` 表支持。

| 配置项 | 默认值 | 说明 |
| --- | --- | --- |
| `scaffold.cache.postgresql.table-name` | `scaffold_spring_cache` | 缓存表，可包含 schema |
| `default-ttl` | `3d` | 默认有效期，非正值表示不过期 |
| `unlogged` | `true` | 是否创建 UNLOGGED 表 |
| `initialize-schema` | `true` | 是否自动建表、建索引 |
| `cleanup-on-startup` | `true` | 启动时清理过期记录 |
| `scheduled-cleanup` | `true` | 是否定时清理 |
| `cleanup-interval` | `5m` | 清理间隔 |

直接依赖本模块时，需要由应用自行声明存储与缓存管理器 Bean。
