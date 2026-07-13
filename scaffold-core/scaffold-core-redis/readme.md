# scaffold-core-redis

Redis 公共能力模块，集成 Redisson，并提供缓存、Redis Stream、Pub/Sub 和常用操作封装。

## 主要能力

- `RedisUtils`：配置 `spring.data.redis.host` 后生效的常用操作封装。
- `RedisStreamListener`、`RedisSubTopic`：声明消息监听器。
- `RedisMessageQueueRegister` 与消息发送辅助类。
- Redis 缓存序列化基础配置。

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
  cache:
    type: redis
    redis.time-to-live: 30m
```

本模块不自定义 `CacheManager`。生产环境还应配置密码、TLS、连接池和超时。
