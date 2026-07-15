# scaffold-sse-starter

Spring Boot MVC SSE Starter，支持用户多连接、用户定向推送、房间广播、心跳、慢客户端隔离以及 Local、Redis、Kafka 三种消息 Broker。

## 使用

```xml
<dependency>
    <groupId>com.scaffold</groupId>
    <artifactId>scaffold-sse-starter</artifactId>
</dependency>
```

默认使用本机内存 Broker：

```yaml
scaffold:
  sse:
    broker: local
    connection-timeout: 30m
    heartbeat-interval: 25s
    queue-capacity: 100
```

业务应用自行提供连接接口，并从可信登录上下文取得用户 ID：

```java
@GetMapping(value = "/sse/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public SseEmitter connect(String userId, List<String> roomId) {
    return connectionManager.connect(userId, roomId);
}
```

```java
connectionManager.sendToUser(userId, "notice", data);
connectionManager.sendToRoom(roomId, "chat", data);
connectionManager.broadcast("system", data);
```

## Redis

```yaml
scaffold:
  sse:
    broker: redis
    redis:
      channel: scaffold:sse:messages
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

Redis 使用 Pub/Sub。每个实例订阅相同 Channel，收到消息后只投递本节点持有的连接。

## Kafka

```yaml
scaffold:
  sse:
    broker: kafka
    kafka:
      topic: scaffold-sse-messages
      # 每个 SSE 服务实例必须使用不同消费组
      group-id: scaffold-sse-node-1
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      auto-offset-reset: latest
```

Kafka 必须为每个 SSE 节点配置不同的 `group-id`，这样每个节点都能收到消息并检查自己的本地连接。未配置时 Starter 会在每次启动时生成随机消费组。

真实的 `SseEmitter` 和 TCP 连接始终保存在建立连接的 JVM 内存中；Redis 和 Kafka 只负责跨节点传递 `SseMessage`，不能保存物理连接。
