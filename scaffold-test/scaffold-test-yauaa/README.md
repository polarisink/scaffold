# YAUAA User-Agent 解析示例

使用 Yet Another UserAgent Analyzer（YAUAA）解析请求头，并返回所有可用字段，例如设备类型、操作系统、浏览器名称和版本。

```bash
./mvnw -pl scaffold-test/scaffold-test-yauaa -am -Pexamples spring-boot:run
curl -H 'User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) AppleWebKit/605.1.15 Mobile/15E148' \
  http://localhost:8018/yauaa
```

入口为 `GET /yauaa`，`User-Agent` 请求头必填。首次构建或启动可能因加载 YAUAA 规则库耗时稍长。
