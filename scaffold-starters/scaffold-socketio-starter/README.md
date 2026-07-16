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
  websocket:
    enabled: true
    host: 127.0.0.1
    port: 8081
    context: ""
    transports: websocket,polling
```

`enabled` 默认为 `true`。若要自定义连接认证，在应用中声明一个
`AuthorizationListener` Bean 即可替换默认实现。完整示例见
`scaffold-test/scaffold-test-socketio`。
