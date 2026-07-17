# scaffold-ai-starter

从 MateCloud `mate-ai-starter` 提取并适配 Spring Boot 3.5 / Spring AI 1.1 的 AI Starter。

提供同步聊天、SSE 流式聊天、按会话 ID 保存的窗口记忆、Advisor 链、`@Tool` 自动发现、
工具清单与直接调用 API。

```xml
<dependency>
    <groupId>com.scaffold</groupId>
    <artifactId>scaffold-ai-starter</artifactId>
</dependency>
```

Starter 保持模型提供商中立。业务应用按需增加 `spring-ai-starter-model-openai`、
`spring-ai-starter-model-ollama` 等模型依赖；测试模块则提供无需联网的本地 `ChatModel`。

主要接口：

- `POST /api/ai/chat`
- `POST /api/ai/chat/stream`
- `GET /api/ai/tools`
- `POST /api/ai/tools/{name}/invoke`

配置前缀为 `scaffold.ai`。生产环境建议开启 `scaffold.ai.security.enabled` 并通过环境变量
提供 `scaffold.ai.security.api-key`；模型提供商使用 Spring AI 原生 `spring.ai.*` 配置。
