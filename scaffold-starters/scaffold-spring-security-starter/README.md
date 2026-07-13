# scaffold-spring-security-starter

Spring MVC 应用的 Spring Security 无状态认证 Starter。它只负责认证技术装配，不提供登录接口、用户查询或 RBAC 数据模型。

## 主要能力

- 无状态 `SecurityFilterChain`，关闭 Session、表单登录、HTTP Basic 和 CSRF。
- `TokenAuthenticationFilter` 解析 token 并建立认证上下文。
- `DaoAuthenticationProvider`、`AuthenticationManager` 和统一 401/403 响应。
- 可配置认证白名单与 Security CORS。
- 复用 `scaffold-core-auth` 的 JWT、`TokenStore` 和密码编码器。

## 接入

```xml
<dependency>
    <groupId>com.scaffold</groupId>
    <artifactId>scaffold-spring-security-starter</artifactId>
</dependency>
```

应用必须提供 `UserDetailsService`；需要完整后台权限系统时可直接引入 `scaffold-module-rbac-security`。

```yaml
scaffold:
  security:
    ignore-list: [/public/**]
    cors:
      enabled: true
      allowed-origin-patterns: ["http://localhost:*"]
      allowed-methods: [GET, POST, PUT, DELETE, PATCH, OPTIONS]
      allowed-headers: ["*"]
    token:
      jwt-secret: ${SCAFFOLD_JWT_SECRET}
      cache-ttl: 30m
```

不要与 `scaffold-sa-token-starter` 同时引入。生产环境必须替换默认 JWT 密钥，并根据部署方式审慎配置 CORS。
