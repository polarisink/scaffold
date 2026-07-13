# scaffold-web-starter

Spring MVC 业务应用的基础 Starter，适合作为 HTTP API 项目的第一个依赖。

## 主要能力

- Spring MVC、Bean Validation、统一响应与全局异常处理。
- Trace ID、MDC 异步上下文传递和 RestClient 链路透传。
- 可配置的请求/响应日志、慢请求日志与 MVC CORS。
- `bizlog-sdk` 操作日志基础支持。

```xml
<dependency>
    <groupId>com.scaffold</groupId>
    <artifactId>scaffold-web-starter</artifactId>
</dependency>
```

```yaml
scaffold:
  web:
    cors:
      enabled: true
      allowed-origin-patterns: ["http://localhost:*"]
    response:
      raw-body-path-patterns: [/actuator, /actuator/**]
    request-log:
      enabled: true
      slow-threshold-millis: 1000
      max-payload-length: 16384
      exclude-path-patterns: [/actuator/**]
```

CORS 默认关闭，请求日志默认开启。文件下载、流式响应和框架端点应加入 `raw-body-path-patterns`，避免被统一响应模型包装。
