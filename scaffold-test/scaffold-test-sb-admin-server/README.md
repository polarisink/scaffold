# Spring Boot Admin 服务端

提供 Spring Boot Admin 监控控制台，默认监听 `10000`。

```bash
./mvnw -pl scaffold-test/scaffold-test-sb-admin-server -am -Pexamples spring-boot:run
```

启动后访问 <http://localhost:10000>，再启动 `scaffold-test-sb-admin-client` 即可看到实例注册、健康状态和运行指标。

当前服务端未配置登录认证和持久化，只用于本地集成验证，不应直接暴露到公网。
