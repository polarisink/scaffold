# scaffold-core-redis

Redis 底层公共能力模块，提供 Redis Stream、Pub/Sub 和常用操作封装，不主动注册 Spring Bean。

## 主要能力

- `RedisUtils`：常用 Redis 操作封装。
- `RedisStreamListener`、`RedisSubTopic`：声明消息监听器。
- `RedisMessageQueueRegister` 与消息发送辅助类。

业务应用需要缓存时使用 [`scaffold-cache-starter`](../../scaffold-starters/scaffold-cache-starter/README.md)；需要 Stream/Pub/Sub 监听基础设施时使用 [`scaffold-redis-messaging-starter`](../../scaffold-starters/scaffold-redis-messaging-starter/README.md)。

```xml
<dependency>
    <groupId>com.scaffold</groupId>
    <artifactId>scaffold-core-redis</artifactId>
</dependency>
```

```yaml
spring:
  data.redis:
    host: localhost
    port: 6379
```

本模块不启用缓存、消息监听或自定义 `CacheManager`。生产环境还应配置密码、TLS、连接池和超时。
