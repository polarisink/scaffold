# Netty Socket.IO 示例

演示基于 Netty Socket.IO 的连接、房间和事件监听，并在应用启动时发送一条 Redis 消息。客户端通过查询参数 `trainId` 加入同名房间，监听事件名为 `message`。

## 运行

先启动 Redis，并按需修改 `application.yml` 的主机和密码：

```bash
./mvnw -pl scaffold-test/scaffold-test-netty-socketio -am -Pexamples spring-boot:run
```

应用 Web 端口为 `8081`。打开 <http://localhost:8081/socket-test.html> 使用测试页面连接。Socket.IO 服务的实际监听配置来自 `scaffold-core-websocket` 自动配置；如连接失败，检查 Starter 的 host、port 和 path 配置，而不是把 Web 端口默认视为 Socket.IO 端口。

当前 `message` 处理器为空，示例重点是生命周期、房间管理和优雅停止。配置文件中的 Redis 密码只是本地示例，使用前应通过环境配置覆盖。
