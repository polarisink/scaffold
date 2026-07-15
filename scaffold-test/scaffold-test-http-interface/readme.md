# Spring HTTP Interface 对比示例

在同一应用中对比三种 HTTP 客户端：Retrofit、Spring `RestClient` 和响应式 `WebClient`。应用启动后 `Runner` 会分别调用远端 `hello`，并订阅 WebClient 的 SSE 流。

## 前置服务

三组客户端的基础地址均硬编码为 `http://localhost:8080`，远端需提供对应 API 接口中声明的路径。未启动远端时，本应用仍能启动，但控制台会打印连接异常。

```bash
./mvnw -pl scaffold-test/scaffold-test-http-interface -am -Pexamples spring-boot:run
```

本应用监听 `8888`，但自身不提供业务 Controller。若要切换远端地址，修改 `RemoteConfig.baseUrl`；正式项目建议改为配置属性。Retrofit 中标量转换器必须先于 Jackson 注册，否则纯文本响应可能被当作 JSON 解析。
