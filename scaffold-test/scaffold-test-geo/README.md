# Geo Starter 依赖验证

本模块目前只引入 `scaffold-geo-starter`，用于验证 Starter 的依赖解析和自动配置装配；尚未提供应用入口、接口或测试用例，因此不能独立启动。

构建验证：

```bash
./mvnw -pl scaffold-test/scaffold-test-geo -am -Pexamples package
```

如需可运行的空间查询示例，请使用相邻的 `scaffold-test-postgresql-geo`；如需为 Starter 增加冒烟测试，可在本模块补充 `@SpringBootTest` 和最小应用入口。
