# scaffold-test-ai

`ai-starter` 的可运行示例与集成测试，默认使用本地回声 `ChatModel`，无需 API Key。

```bash
./mvnw -pl scaffold-test/scaffold-test-ai -am -Pexamples spring-boot:run
```

打开 <http://localhost:8101>。如需连接真实模型，增加 `spring-ai-starter-model-openai`
依赖，删除 `DemoAiConfiguration` 中的本地 `ChatModel`/`ChatClient.Builder`，将
`spring.ai.model.chat` 改为 `openai`，并配置：

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      # OpenAI 兼容服务可配置 base-url
      base-url: https://api.openai.com
      chat:
        options:
          model: gpt-4o-mini
```
