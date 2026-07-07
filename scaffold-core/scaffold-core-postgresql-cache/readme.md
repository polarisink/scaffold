# PostgreSQL Spring Cache

该模块只包含基于 PostgreSQL 表的 Spring Cache 实现，不提供 Caffeine、Redis 或多级缓存。
业务项目通常应依赖 `scaffold-starter-postgresql-cache`。

主要能力：

- 使用 `JdbcTemplate` 读写缓存表
- 支持 TTL、启动清理和定时清理
- 支持 PostgreSQL `UNLOGGED` 缓存表
- `PostgresqlCacheManager` 由应用显式声明，不参与 `spring.cache.type` 自动选择

配置项使用 `scaffold.cache.postgresql.*`，完整用法见 starter 文档。
