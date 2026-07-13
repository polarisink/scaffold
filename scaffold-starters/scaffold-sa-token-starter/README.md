# scaffold-sa-token-starter

Spring MVC 应用的 Sa-Token 认证 Starter。它注册全局 `SaInterceptor`，除白名单外的所有请求都要求已登录。

## 接入

```xml
<dependency>
    <groupId>com.scaffold</groupId>
    <artifactId>scaffold-sa-token-starter</artifactId>
</dependency>
```

```yaml
scaffold:
  security:
    ignore-list:
      - /public/**

sa-token:
  token-name: Authorization
  timeout: 2592000
  active-timeout: 1800
  is-concurrent: true
```

`scaffold.security.ignore-list` 会追加到框架默认白名单。登录、登出、用户模型和权限判断由业务模块实现；需要完整后台权限系统时使用 `scaffold-module-rbac-sa-token`。

不要与 `scaffold-spring-security-starter` 同时引入。Sa-Token 的持久化与会话策略应根据部署方式配置，集群部署时需要共享存储。
