# scaffold-test-native

用于验证 scaffold 各 starter 在 GraalVM Native Image 中能否正常启动和运行。

## SSE 与 Socket.IO 测试页面

启动应用后访问：

```text
http://localhost:8082/
```

页面提供以下测试能力：

- 建立 SSE 连接，并向指定用户或房间发送测试事件。
- 查看 SSE 在线用户数和连接数。
- 连接 Socket.IO 服务并验证 `server-ready` 事件。
- 发送 `native-echo` 事件，验证服务端事件响应和 ACK。

Socket.IO 测试监听器通过 `SocketIOServer.addConnectListener`、`addDisconnectListener` 和
`addEventListener` 编程式注册，业务监听方法不依赖反射，因此无需单独维护 RuntimeHints。

默认情况下 HTTP 服务使用 `8082` 端口，Socket.IO 使用 `8081` 端口。页面会自动使用当前 HTTP 端口的前一个端口连接 Socket.IO，也可以手动修改连接地址。

Socket.IO 浏览器客户端由 CDN 加载，因此测试页面需要能够访问互联网；SSE 测试不依赖外部资源。

## 构建

脚本说明见 [scripts/README.md](scripts/README.md)。
