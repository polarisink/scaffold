# scaffold-redis-messaging-starter

Redis Stream 与 Pub/Sub 消息基础设施 Starter，同时传递引入 `scaffold-core-redis`。

消息监听默认关闭，按需启用：

```yaml
scaffold:
  redis:
    messaging:
      enabled: true
```

启用后自动注册 Stream 监听容器、监听注解扫描与 Pub/Sub 容器。Redis 连接仍使用 Spring Boot 的 `spring.data.redis` 配置。
