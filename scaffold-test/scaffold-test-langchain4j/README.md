# LangChain4j 最小示例

本模块用于验证 `langchain4j-spring-boot-starter` 与 Scaffold Web Starter 可以共同启动。当前只有 Spring Boot 入口，没有配置模型供应商、AI Service 或 HTTP 接口。

```bash
./mvnw -pl scaffold-test/scaffold-test-langchain4j -am -Pexamples spring-boot:run
```

应用默认监听 `8082`。要进行实际对话，需要按所选模型供应商增加对应 LangChain4j 依赖和 API Key/模型配置，再定义 `@AiService` 或注入 ChatModel。API Key 应通过环境变量传入，不要提交到仓库。
