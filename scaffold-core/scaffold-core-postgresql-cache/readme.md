# spring cache

该模块只包含 PostgreSQL Cache 的核心实现类。Spring Boot 自动装配已放到
`scaffold-starter-postgresql-cache`，业务项目建议依赖 starter。

- 支持 Caffeine 本地缓存
- 支持 PostgreSQL `UNLOGGED` 表缓存
- 支持 Caffeine + PostgreSQL 两级缓存
- PostgreSQL 模式适合会话缓存、接口结果缓存、热点数据缓存等不要求亚毫秒延迟的场景
- `postgresql` 和 `two-level` 模式使用 `JdbcTemplate` 访问缓存表
- `two-level` 的集群失效监听需要 PostgreSQL `DataSource`

配置示例：

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

`two-level` 模式下会使用 PostgreSQL `LISTEN/NOTIFY` 广播本地 Caffeine 失效事件。
某个节点执行 `evict`、`clear` 或更新缓存后，会先更新 PostgreSQL，再发送通知；其他节点收到通知后只清自己的 L1 缓存。
