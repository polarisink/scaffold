# SSE 业务推送示例

启动 `SseApplication` 后访问 <http://localhost:8080/>，页面会以 `user-1` 身份连接并加入 `room-a`。

连接接口必须使用 GET（浏览器 `EventSource` 的要求）：

```text
GET /api/sse/connect?userId=user-1&roomId=room-a&roomId=room-b
```

向指定用户的所有在线终端发送消息：

```bash
curl -X POST http://localhost:8080/api/sse/users/user-1/messages \
  -H 'Content-Type: application/json' \
  -d '{"event":"notice","data":{"title":"订单已支付","orderId":"10001"}}'
```

向房间内所有在线连接广播消息：

```bash
curl -X POST http://localhost:8080/api/sse/rooms/room-a/messages \
  -H 'Content-Type: application/json' \
  -d '{"event":"chat","data":{"sender":"system","content":"房间消息"}}'
```

业务代码可直接注入 `SseConnectionManager`：

```java
connectionManager.sendToUser(userId, "order-status", orderStatus);
connectionManager.sendToRoom(roomId, "room-message", roomMessage);
```

每条连接拥有独立的有界发送队列和虚拟发送线程，业务线程调用推送方法时只进行非阻塞入队，不会等待客户端网络写入。默认每连接最多积压 100 条消息：

```properties
scaffold.sse.queue-capacity=100
scaffold.sse.heartbeat-interval=25000
```

队列满表示客户端消费速度长期落后，该连接会被主动断开并从用户、房间索引中删除。`sendToUser`、`sendToRoom` 和 `broadcast` 的返回值表示成功入队的连接数，不表示客户端已经收到消息。单条连接按入队顺序发送，不同连接之间互不阻塞。

示例为了便于测试从请求参数读取 `userId`。生产环境必须从 Spring Security 登录上下文或可信 Token 中取得用户 ID，避免冒充其他用户。当前管理器存储的是本机内存连接；集群部署时，各节点应通过 Redis Pub/Sub、RocketMQ 或 Kafka 分发业务消息，再由持有目标 SSE 连接的节点完成推送。

## 扩展 Redis 或 Kafka

业务代码统一依赖 `SseMessageBroker`。默认配置 `scaffold.sse.broker=local` 并装配 `LocalSseMessageBroker`。后续接入消息中间件时，将配置改为 `kafka` 或 `redis`，并声明自己的 Bean：

```properties
scaffold.sse.broker=kafka
```

```java
@Bean
SseMessageBroker kafkaSseMessageBroker(KafkaTemplate<String, SseMessage> kafkaTemplate) {
    return message -> {
        kafkaTemplate.send("scaffold-sse", message.messageId(), message);
        return SseSendResult.accepted(message.messageId());
    };
}
```

每个节点的 Kafka 或 Redis 消费者收到消息后调用本节点投递器，不能再次调用 Broker，否则会形成消息循环：

```java
@KafkaListener(topics = "scaffold-sse")
public void consume(SseMessage message) {
    localDispatcher.dispatch(message);
}
```

`SseEmitter` 与 TCP 连接仍由各节点的 `SseConnectionRepository` 保存在本机内存中，不能保存到 Redis 或 Kafka。
