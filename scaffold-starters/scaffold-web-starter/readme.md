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

CORS 默认关闭，请求日志默认开启。文件下载、流式响应和框架端点应加入
`response.raw-body-path-patterns`，避免被统一响应模型包装。

## 请求日志

`RequestLogFilter` 默认注册到所有 URL，并在一次请求处理完成后记录以下字段：

- HTTP 方法和请求 URI；
- 客户端 IP；
- JSON 请求体和 JSON 响应体；
- 请求耗时；
- HTTP 响应状态码。

只有 `Content-Type` 与 `application/json` 兼容的载荷才会输出内容。文件、HTML、表单和其他
非 JSON 数据不会写入日志，以避免二进制内容或无关文本污染日志。请求体和响应体超过
`max-payload-length` 后会截断，并追加 `... [truncated]`。

耗时小于等于 `slow-threshold-millis` 的请求使用 INFO 级别；超过阈值的请求使用 WARN 级别。
日志记录失败不会改变业务响应。

### 配置项

| 配置项 | 默认值 | 说明 |
| --- | --- | --- |
| `scaffold.web.request-log.enabled` | `true` | 是否注册请求日志过滤器 |
| `scaffold.web.request-log.slow-threshold-millis` | `1000` | 慢请求阈值，单位毫秒 |
| `scaffold.web.request-log.max-payload-length` | `16384` | 单个请求体或响应体最多记录的字节数；负数按 `0` 处理 |
| `scaffold.web.request-log.exclude-path-patterns` | `[]` | 不记录日志的 Ant 风格路径集合 |

例如排除健康检查、指标和文件下载接口：

```yaml
scaffold:
  web:
    request-log:
      exclude-path-patterns:
        - /actuator/**
        - /api/files/**
```

如需完全关闭请求日志：

```yaml
scaffold:
  web:
    request-log:
      enabled: false
```

### SSE 与流式响应

浏览器 `EventSource` 会发送 `Accept: text/event-stream`。过滤器检测到该媒体类型后会自动跳过
请求和响应缓存，保证 SSE 使用分块传输并持续保持连接。因此 SSE 接口无需额外加入
`request-log.exclude-path-patterns`。

使用自定义 SSE 客户端时必须发送正确的请求头：

```http
Accept: text/event-stream
```

其他不使用 `text/event-stream` 的流式接口（例如 `StreamingResponseBody`、大文件下载）建议加入
`request-log.exclude-path-patterns`。该配置控制请求日志过滤；
`response.raw-body-path-patterns` 控制统一响应包装，两者用途不同，流式接口可能需要同时配置。

### 日志示例

```text
************************************************************************
* Request URI:      POST /api/orders
* Ip:               127.0.0.1
* Request Body:     {"productId":1001,"quantity":2}
* Time Consume:     37 ms
* Response Status:  200
* Response Body:    {"code":0,"data":{"orderId":"O-10001"}}
************************************************************************
```

请求日志可能包含业务数据。密码、令牌等敏感字段应在进入日志过滤器前完成脱敏，或者将对应接口加入
`exclude-path-patterns`；生产环境不要依赖载荷截断实现敏感信息保护。
