# scaffold-core-auth

Spring Security 与 Sa-Token 共用的认证基础模块，不包含登录接口或 RBAC 业务。

## 主要能力

- `SecurityProperties`：认证忽略路径、CORS、JWT 与 token 缓存配置。
- `JwtUtil`、`PayloadDTO`：JWT 签发、解析及载荷模型。
- `TokenStore`：token 存储抽象。
- `SpringCacheTokenStore`：基于 Spring Cache 的默认实现，缓存名为 `security_token`。
- 默认 `PasswordEncoder` 和通用认证错误码。

业务应用一般不直接依赖本模块，请选择：

- [`scaffold-spring-security-starter`](../../scaffold-starters/scaffold-spring-security-starter/README.md)
- [`scaffold-sa-token-starter`](../../scaffold-starters/scaffold-sa-token-starter/README.md)

## 公共配置

```yaml
scaffold:
  security:
    ignore-list:
      - /public/**
    token:
      jwt-secret: ${SCAFFOLD_JWT_SECRET}
      cache-ttl: 30m
      cache-maximum-size: 10000
```

`ignore-list` 会追加到框架默认白名单，而不是覆盖默认值。生产环境中的 JWT 密钥应通过环境变量等外部配置提供，并使用至少 32 字节的随机值。
