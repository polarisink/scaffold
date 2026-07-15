# Spring Boot + Vert.x 多协议服务

演示由 Spring 容器创建和注入 Verticle，再由 `VertxDeployer` 并行部署 HTTP、TCP、UDP、WebSocket 服务。每类服务默认 4 个虚拟线程实例，应用关闭时统一关闭 Vert.x。

```bash
./mvnw -pl scaffold-test/scaffold-test-vertx-spring -am -Pexamples spring-boot:run
```

该应用配置为非 Web Spring Boot 应用，网络监听完全由 Vert.x 提供。地址、端口等属性绑定在 `net.*`（`NetProperties`）；当前 `application.yml` 仅设置进程保活，运行前应核对 `NetProperties` 的默认值或显式补充配置。

HTTP 服务提供 `GET /api/user/{id}`、`POST /api/save`、`GET /udp` 和 `DELETE /ws/kick/{id}`。多实例端口复用依赖操作系统能力；如平台不支持，应减少部署实例数。
