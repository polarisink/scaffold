# scaffold-core-sse

SSE 核心运行时，不包含 Spring Boot 自动配置和具体消息中间件实现。

包含：

- `SseConnectionManager`：连接和消息操作门面
- `SseConnection`：单连接有界队列与虚拟发送线程
- `SseConnectionRepository`：本节点连接仓库抽象
- `InMemorySseConnectionRepository`：本地内存仓库
- `SseLocalDispatcher`：当前节点最终消息投递
- `SseMessageBroker`：消息发布 SPI
- `LocalSseMessageBroker`：单机消息实现
- `SseMessage`、`SseSendResult`：与中间件无关的消息模型

业务应用通常不直接引用该模块，而是引用 `scaffold-sse-starter`。Starter 负责配置属性、自动装配、心跳调度以及 Local、Redis、Kafka Broker 的选择。

真实的 `SseEmitter` 和 TCP 连接只能保存在建立连接的 JVM 内存中。Redis、Kafka 等中间件只负责跨节点传播 `SseMessage`。
