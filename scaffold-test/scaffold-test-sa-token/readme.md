# Sa-Token 登录示例

演示 Sa-Token 的登录、会话状态、Token 信息和注销。示例账号固定为 `zhang / 123456`，仅用于本地测试。

```bash
./mvnw -pl scaffold-test/scaffold-test-sa-token -am -Pexamples spring-boot:run
curl -i -X POST 'http://localhost:8081/user/doLogin?username=zhang&password=123456'
```

保留响应中的 `satoken` Cookie，继续请求：

```bash
curl -b 'satoken=<token>' http://localhost:8081/user/isLogin
curl -b 'satoken=<token>' http://localhost:8081/user/tokenInfo
curl -b 'satoken=<token>' -X POST http://localhost:8081/user/logout
```

`application.yml` 配置了 30 天有效期、允许并发登录但每次登录生成独立 Token。生产环境必须接入真实用户校验、HTTPS、权限注解和安全的 Token 存储策略。
