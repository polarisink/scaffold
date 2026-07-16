# scaffold-socketio-starter

基于 `netty-socketio` 的 Spring Boot Starter。引入依赖后会自动创建并启动
`SocketIOServer`，并在 Spring 容器关闭时停止服务。

## 主要能力

- `WebSocketAutoConfiguration`：Socket.IO 服务端自动配置。
- `WebSocketProperties`：类型安全的 `scaffold.socketio` 配置。
- `SocketAuthListener`：默认放行的连接认证实现，可由业务 Bean 覆盖。
- `WsManager`：连接管理与消息推送辅助方法。

```xml
<dependency>
    <groupId>com.scaffold</groupId>
    <artifactId>scaffold-socketio-starter</artifactId>
</dependency>
```

```yaml
scaffold:
  socketio:
    enabled: true
    host: 127.0.0.1
    port: 8081
    context: ""
    transports: websocket,polling
```

`enabled` 默认为 `true`。若要自定义连接认证，在应用中声明一个
`AuthorizationListener` Bean 即可替换默认实现。完整示例见
`scaffold-test/scaffold-test-socketio`。

## Netty 版本兼容性

`netty-socketio 2.0.14` 基于 Netty 4.1 构建。当前 `scaffold-dependencies` 将 Netty
全局固定为 `4.1.130.Final`，这会同时覆盖 Spring Boot 为所有应用管理的 Netty
版本。

后续升级或排查 Spring Cloud Gateway、WebFlux/Reactor Netty、Vert.x、Dubbo、
Redisson 等组件时，需要优先检查该全局约束。长期建议是在 Socket.IO 服务端支持
Spring Boot 当前 Netty 主版本后统一升级，或将 Netty 4.1 覆盖移入 Socket.IO 专用应用/BOM，
避免影响其他 Spring 应用。
