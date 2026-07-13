# scaffold-openapi-starter

基于 Knife4j OpenAPI 3 的接口文档 Starter。

## 接入

```xml
<dependency>
    <groupId>com.scaffold</groupId>
    <artifactId>scaffold-openapi-starter</artifactId>
</dependency>
```

```yaml
swagger:
  enabled: true
  title: Scaffold API
  description: 后台管理接口
  version: v1.0
  group-name: default
  base-package: com.example
  base-path: [/**]
  exclude-path: [/error]
  contact:
    name: maintainer
    email: maintainer@example.com
```

`swagger.enabled` 默认是 `false`。启用后可通过 `/doc.html` 访问 Knife4j，通过 `/v3/api-docs` 获取 OpenAPI JSON。

生产环境建议保持关闭，或在网关和网络层限制文档端点访问。认证项目还需确保相应文档路径位于认证白名单中。
