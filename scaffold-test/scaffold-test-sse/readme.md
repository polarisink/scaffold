# SSE 业务推送示例

启动 `SseApplication` 后访问 <http://localhost:8100/>，页面会以 `user-1` 身份连接并加入 `room-a`。

连接接口必须使用 GET（浏览器 `EventSource` 的要求）：

```text
GET /api/sse/connect?userId=user-1&roomId=room-a&roomId=room-b
```

向指定用户的所有在线终端发送消息：

```bash
curl -X POST http://localhost:8100/api/sse/users/user-1/messages \
  -H 'Content-Type: application/json' \
  -d '{"event":"notice","data":{"title":"订单已支付","orderId":"10001"}}'
```

向房间内所有在线连接广播消息：

```bash
curl -X POST http://localhost:8100/api/sse/rooms/room-a/messages \
  -H 'Content-Type: application/json' \
  -d '{"event":"chat","data":{"sender":"system","content":"房间消息"}}'
```

业务代码可直接注入 `SseConnectionManager`：

```java
connectionManager.sendToUser(userId, "order-status", orderStatus);
connectionManager.sendToRoom(roomId, "room-message", roomMessage);
```

客户端从 `connected` 事件中取得 `connectionId` 后，可主动断开当前用户的指定连接：

```text
DELETE /api/sse/connections/{connectionId}?userId=user-1
```

业务代码对应调用 `connectionManager.disconnect(userId, connectionId)`。生产环境的 `userId` 必须来自可信登录上下文；管理器会校验连接归属，不能用该接口断开其他用户的连接。

每条连接拥有独立的有界发送队列和虚拟发送线程，业务线程调用推送方法时只进行非阻塞入队，不会等待客户端网络写入。默认每连接最多积压 100 条消息：

```properties
scaffold.sse.queue-capacity=100
scaffold.sse.heartbeat-interval=25000
```

队列满表示客户端消费速度长期落后，该连接会被主动断开并从用户、房间索引中删除。`sendToUser`、`sendToRoom` 和 `broadcast` 的返回值表示成功入队的连接数，不表示客户端已经收到消息。单条连接按入队顺序发送，不同连接之间互不阻塞。

示例为了便于测试从请求参数读取 `userId`。生产环境必须从 Spring Security 登录上下文或可信 Token 中取得用户 ID，避免冒充其他用户。当前管理器存储的是本机内存连接；集群部署时，各节点应通过 Redis Pub/Sub、RocketMQ 或 Kafka 分发业务消息，再由持有目标 SSE 连接的节点完成推送。

核心能力已经迁移到 `scaffold-sse-starter`。本测试项目只保留应用入口、业务 Controller、测试页面和集成测试。Redis、Kafka 的切换配置参见 Starter README。

运行与测试：

```bash
./mvnw -pl scaffold-test/scaffold-test-sse -am -Pexamples spring-boot:run
./mvnw -pl scaffold-test/scaffold-test-sse -am -Pexamples test
```
