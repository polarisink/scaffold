# Spring Boot Admin 客户端

作为被监控应用向本地 Spring Boot Admin Server 注册，默认端口 `9999`，注册地址为 `http://localhost:10000`。

先启动服务端，再启动本模块：

```bash
./mvnw -pl scaffold-test/scaffold-test-sb-admin-server -am -Pexamples spring-boot:run
./mvnw -pl scaffold-test/scaffold-test-sb-admin-client -am -Pexamples spring-boot:run
```

客户端暴露 `health`、`info`、`metrics`、`env`、`loggers`、线程和堆转储等 Actuator 端点。当前示例没有鉴权，只适合本机开发；生产环境必须限制敏感端点并为 Admin Server 和客户端注册配置认证。
