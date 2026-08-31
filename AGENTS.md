# Scaffold 开发指南

本文件适用于仓库根目录及全部子目录。开始修改前先阅读 `README.md`、根 `pom.xml`
以及目标模块的 README；若子目录存在更具体的 `AGENTS.md`，以子目录约定为准。

## 项目定位

本仓库是 Java 21、Spring Boot 4.1 的 Maven 多模块项目脚手架，提供可复用的基础模块、
Starter、RBAC 模块、示例应用和 Vue 管理端。修改时应保持组件通用性，不把某个业务项目
的领域规则、固定 URL、账号或部署环境写入公共模块。

主要目录：

- `scaffold-dependencies`：全局 BOM 和第三方依赖版本。
- `scaffold-core`：基础类型、认证、ORM、Redis、SSE、地理能力等底层模块。
- `scaffold-starters`：Spring Boot 自动配置和可选集成。
- `scaffold-module`：可组合的业务模块，目前包含代码生成和 RBAC。
- `scaffold-biz`：脚手架应用装配示例。
- `scaffold-test`：单体功能示例，默认不参与根 reactor 构建。
- `scaffold-test-cloud`：Spring Cloud 示例，通过 `examples-cloud` profile 启用。
- `vue-vben-admin`：基于 pnpm workspace 的 Vue 管理端。
- `docker`、`shell`：本地基础设施和部署示例。

## 依赖与构建约定

- Java 版本统一为 21，不在子模块单独覆盖编译版本。
- Spring Boot、Spring Cloud、Spring Cloud Alibaba、Spring AI、Netty 及通用第三方依赖
  统一在 `scaffold-dependencies/pom.xml` 管理。
- 子模块依赖已纳入 BOM 的组件时不显式声明版本；确需覆盖时，应说明兼容性原因和影响范围。
- Spring Boot 4 使用模块化 Starter 和自动配置包；新增代码不要重新引入 Boot 3 的旧包名或
  `spring-boot3-starter`。
- Jackson 3 是默认 JSON 实现。业务代码使用 `tools.jackson.databind.json.JsonMapper`；
  Jackson 注解仍使用 `com.fasterxml.jackson.annotation`。不要新增 Jackson 2
  `ObjectMapper`，除非第三方组件明确要求并已完成隔离。
- Netty 版本必须与 Spring Boot BOM 保持一致。修改时同时检查 Gateway、WebFlux、
  Reactor Netty、Dubbo、Redisson、Vert.x 和 Socket.IO 的兼容性。
- 不随意新增 Maven 仓库、快照版本或重复 BOM。

## 模块设计

- `scaffold-core` 不依赖具体应用模块，避免引入 Controller 或业务流程。
- Starter 只负责自动配置、属性绑定和通用适配，不保存应用级状态。
- Starter 自动配置使用 `@AutoConfiguration`，并在
  `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 中登记。
- 可选能力使用 `@ConditionalOnClass`、`@ConditionalOnBean`、
  `@ConditionalOnProperty` 或 `@ConditionalOnMissingBean`，避免缺少可选依赖时启动失败。
- 配置项使用类型安全的 `@ConfigurationProperties`，统一使用清晰的 `scaffold.*` 前缀。
- 公共扩展点优先提供接口或可替换 Bean，避免应用必须复制自动配置才能定制。
- 模块依赖保持单向，禁止为了复用少量代码形成循环依赖。

## Java 编码约定

- 优先使用构造器注入；可使用 Lombok `@RequiredArgsConstructor`，不新增字段注入。
- 对外部输入和配置做显式校验，异常信息应包含失败对象和处理阶段，但不得泄漏密钥。
- 新代码使用当前 Spring Framework 7、Spring Security 7 和 Spring Data API，避免调用
  已标记废弃或待删除的方法。
- JSON 配置使用 `JsonMapper.builder()` 或 Boot 提供的 `JsonMapper` Bean，避免运行时修改
  已构建的 mapper。
- 日志使用参数化占位符，不使用 `System.out` 输出业务日志，不记录密码、Token、Cookie、
  私钥或带签名的临时 URL。
- 不提交本地绝对路径、IDE 私有配置、生成目录、构建产物或真实环境凭据。
- 保持用户已有修改；不清理、重置或覆盖与当前任务无关的脏工作区文件。
- 数据库实体需要同时兼容JPA和MyBatis-Plus，使用@Comment添加注释

## Starter 与配置修改检查

修改 Starter 时至少确认：

1. 自动配置导入文件包含正确类名。
2. 缺少可选依赖时条件注解能够安全回退。
3. 用户自定义 Bean 可以覆盖默认实现，且不会产生同类型 Bean 歧义。
4. `@ConfigurationProperties` 默认值、配置元数据和 README 示例保持一致。
5. Servlet、Reactive 或云环境专用配置不会污染其他应用类型。
6. AOT 或 Native Image 涉及反射、资源和代理时同步维护 Runtime Hints。

## 测试与验证

根据修改范围选择最小验证集：

```bash
./mvnw test
./mvnw -pl <module> -am test
./mvnw -Pexamples test
./mvnw -Pexamples-cloud test
git diff --check
```

- 公共工具类优先添加快速单元测试。
- Starter 使用 `ApplicationContextRunner` 或对应 Web Context Runner 验证条件装配和覆盖行为。
- Controller、安全、序列化和持久化变更应覆盖成功、边界与失败场景。
- 默认测试不得依赖公网、个人凭据或本机已启动的中间件；需要基础设施时使用明确的集成测试配置。
- Cloud、Docker、Native Image 和需要真实中间件的验证成本较高，只在相关变更中执行并说明前置条件。
- 自动化代理是否可以运行构建或测试，以用户指令和仓库内更高优先级规则为准。

## 前端与文档

- `vue-vben-admin` 使用仓库现有的 pnpm、ESLint 和格式化配置，不混用 npm 或 yarn lockfile。
- 修改后端接口时同步检查前端类型、请求封装和示例页面。
- 新增或修改公共 Starter、配置项、依赖要求及运行方式时同步更新对应 README。
- 架构或模块边界发生变化时更新 `docs/architecture.md`；路线变化更新 `docs/roadmap.md`。
- 文档以中文为主，类名、配置键、协议和框架名保留原文。
