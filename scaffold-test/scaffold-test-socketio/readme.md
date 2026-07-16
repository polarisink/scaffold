# Netty Socket.IO 示例

演示基于 Netty Socket.IO 的连接、房间和事件监听。客户端通过查询参数 `trainId` 加入同名房间，监听事件名为 `message`。

## 运行

```bash
./mvnw -pl scaffold-test/scaffold-test-socketio -am -Pexamples install -DskipTests
./mvnw -f scaffold-test/scaffold-test-socketio/pom.xml spring-boot:run
```

浏览器测试页由 Spring MVC 提供，Web 端口是 `8081`：

<http://localhost:8081/socket-test.html>

页面再连接独立的 Socket.IO 端口 `8101`。Socket.IO 服务配置来自
`scaffold-socketio-starter` 的 `scaffold.websocket` 配置。

IDEA 中重启时应先停止旧的 `WebSocketApplication` 进程，并关闭运行配置的
`Allow multiple instances`。如果报 `Address already in use`，可使用以下命令确认占用 `8101`
端口的旧进程：

```bash
lsof -nP -iTCP:8101 -sTCP:LISTEN
```

`message` 处理器会将收到的数据回显给当前客户端，可用于验证连接、事件监听和消息发送。
