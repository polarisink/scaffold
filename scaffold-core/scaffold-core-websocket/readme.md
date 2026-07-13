# scaffold-core-websocket

基于 `netty-socketio` 的 Socket.IO 服务端基础模块。

## 主要能力

- `NettySocketConfig`：Socket.IO 服务端配置。
- `SocketAuthListener`：连接认证监听器扩展点。
- `WsManager`：连接管理与消息推送辅助方法。

```xml
<dependency>
    <groupId>com.scaffold</groupId>
    <artifactId>scaffold-core-websocket</artifactId>
</dependency>
```

当前模块不提供独立 Starter。引入前应检查监听地址、端口及认证逻辑，并在应用生命周期中正确启动和关闭服务。完整示例见 `scaffold-test/scaffold-test-netty-socketio`。
