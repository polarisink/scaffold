# Redis Stream 示例

演示 Scaffold Redis 组件发送和消费 Redis Stream 消息。`GET /` 向 `user` 主题发送一个 `User` 对象；`Consumer` 分别展示直接反序列化业务对象和接收 `RedisMessage<User>` 包装消息。

先启动 Redis，默认地址为 `127.0.0.1:6379`：

```bash
./mvnw -pl scaffold-test/scaffold-test-redis -am -Pexamples spring-boot:run
curl http://localhost:8080/
```

请求本身无响应体，消费结果打印到应用控制台。需要认证或远程 Redis 时，在 `spring.data.redis` 下补充 port、username、password 等配置。模块同时引入 JetCache，但当前示例未定义 JetCache 缓存。
