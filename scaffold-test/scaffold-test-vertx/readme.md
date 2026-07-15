# 原生 Vert.x 多协议服务

不依赖 Spring Boot，使用 Vert.x 同时部署 HTTP、TCP、UDP 和 WebSocket 服务。每类服务启动 4 个虚拟线程 Verticle 实例，并通过端口复用分担连接。

默认监听：WebSocket `localhost:9076`、UDP `9077`、TCP `9078`、HTTP `9080`。配置位于 `src/main/resources/config.yaml`。

```bash
./mvnw -pl scaffold-test/scaffold-test-vertx -am -Pexamples package
java -jar scaffold-test/scaffold-test-vertx/target/scaffold-test-vertx-1.0-SNAPSHOT.jar
curl http://localhost:9080/api/user/42
curl -X POST http://localhost:9080/api/save -H 'Content-Type: application/json' -d '{"name":"demo"}'
```

另有 `GET /udp` 通过 EventBus 请求 UDP 发送，`DELETE /ws/kick/{id}` 广播踢出 WebSocket 用户。多实例依赖操作系统支持 `SO_REUSEPORT`；不支持时应将实例数降为 1。
