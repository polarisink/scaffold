# Contributing

## 模块分层约束

- `core` 只放基础实现、公共抽象和底层能力
- `starter` 只放自动装配、第三方接入和默认策略
- `module` 放可选业务能力，要求引入即生效、移除不残留
- `scaffold-test` 仅用于实验与示例，不进入默认交付链路

## 新增 Starter Checklist

- 提供 `AutoConfiguration`
- 提供 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- 使用 `@ConditionalOnClass`、`@ConditionalOnProperty`、`@ConditionalOnMissingBean` 控制装配边界
- 提供 `@ConfigurationProperties` 并给出合理默认值
- 至少补 1 个自动装配测试
- README 或模块文档补充接入示例

## 新增业务模块 Checklist

- 不依赖默认启动类中的硬编码扫描
- 显式声明所需 starter
- 补最小集成测试或 smoke test
- 提供启用/禁用方式和必要的初始化说明

## 提交前建议

- `./mvnw -pl scaffold-biz -am test`
- 如涉及实验模块：`./mvnw -Pexamples -pl scaffold-test/<module> -am -DskipTests compile`
