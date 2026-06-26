# Scaffold - Spring Boot 脚手架

面向小团队的 Spring Boot 多模块脚手架，目标是提供一个默认精简、按需装配、可以持续演进的后端基础盘。

## 当前基线

- **Java**: 21
- **Spring Boot**: 3.5.15
- **Spring Cloud**: 2025.0.0
- **Spring Cloud Alibaba**: 2025.0.0.0
- **MyBatis Plus**: 3.5.12
- **MySQL**: 8.0.23
- **Redis**: Redisson
- **认证**: Spring Security / Sa-Token，均按 Servlet 与 WebFlux 分开装配
- **接口文档**: Knife4j OpenAPI3

## 模块分层

```text
scaffold/
├── scaffold-biz/              # 默认业务启动项目
├── scaffold-core/             # 基础能力模块
├── scaffold-starters/         # 技术 starter 装配层
├── scaffold-module/           # 可选业务模块，例如 RBAC
├── scaffold-test/             # 普通示例，仅 examples profile 构建
└── scaffold-test-cloud/       # Spring Cloud 示例，仅 examples-cloud profile 构建
```

分层约束：

- `core` 负责底层能力，不直接表达业务语义
- `starter` 负责技术栈自动装配，不承载 RBAC 这类业务接口
- `module` 负责可选业务能力，引入即可使用，移除不应影响默认启动
- `scaffold-test` 和 `scaffold-test-cloud` 不进入默认构建链路

## Starter 结构

认证 starter 已按“认证技术栈 + Web 运行时”拆分，避免一个 starter 同时带入 Servlet、WebFlux、Spring Security、Sa-Token 多套运行时。

```text
scaffold-starters/
├── scaffold-starter-auth-core
├── scaffold-starter-spring-security-servlet
├── scaffold-starter-spring-security-webflux
├── scaffold-starter-sa-token-servlet
├── scaffold-starter-sa-token-webflux
├── scaffold-starter-web
├── scaffold-starter-swagger
├── scaffold-starter-orm
├── scaffold-starter-file
├── scaffold-starter-observability
├── scaffold-starter-postgresql-cache
└── scaffold-starter-postgresql-job
```

认证选择：

| 场景 | 推荐依赖 |
| --- | --- |
| Spring Boot MVC + Spring Security | `scaffold-starter-spring-security-servlet` |
| Spring Boot WebFlux + Spring Security | `scaffold-starter-spring-security-webflux` |
| Spring Boot MVC + Sa-Token | `scaffold-starter-sa-token-servlet` |
| Spring Boot WebFlux / Gateway + Sa-Token | `scaffold-starter-sa-token-webflux` |

`scaffold-starter-auth-core` 提供公共认证基础能力，包括：

- `SecurityProperties`
- JWT 工具和 payload 模型
- `TokenService`
- memory / redis token store
- 默认 `PasswordEncoder`
- 通用认证错误码

## RBAC 模块

RBAC 已拆成公共数据库层和两套认证实现：

```text
scaffold-module/
├── scaffold-module-rbac-data                    # entity、mapper、数据库登录校验服务
├── scaffold-module-rbac-security-servlet        # Spring Security Servlet 完整 RBAC
├── scaffold-module-rbac-sa-token-servlet        # Sa-Token Servlet 完整 RBAC
├── scaffold-module-rbac-auth-security-webflux   # Spring Security WebFlux 认证接口
└── scaffold-module-rbac-auth-sa-webflux         # Sa-Token WebFlux 认证接口
```

选择方式：

- 使用 Spring Security 体系：引入 `scaffold-module-rbac-security-servlet`
- 使用 Sa-Token 体系：引入 `scaffold-module-rbac-sa-token-servlet`
- 使用 Spring Security WebFlux 认证服务：引入 `scaffold-module-rbac-auth-security-webflux`
- 使用 Sa-Token WebFlux 认证服务：引入 `scaffold-module-rbac-auth-sa-webflux`
- 只复用用户、角色、菜单数据库访问：引入 `scaffold-module-rbac-data`

所有 RBAC 认证模块都复用 `scaffold-module-rbac-data`，避免数据库层重复实现。Servlet 版本提供完整后台管理接口，包含 `/auth`、`/user`、`/role`、`/menu`；WebFlux 版本只提供认证相关接口，避免维护四份管理 CRUD。

## Cloud 示例

`scaffold-test-cloud` 提供 Spring Cloud、Gateway、Nacos、Dubbo、Seata、Sentinel 示例，并新增了 WebFlux 认证服务：

```text
scaffold-test-cloud/
├── scaffold-test-auth-10080      # Sa-Token WebFlux 认证服务
├── scaffold-test-gateway-10000   # Spring Cloud Gateway WebFlux
├── scaffold-test-provider-10081
├── scaffold-test-consumer-10082
├── scaffold-test-dubbo-*
└── scaffold-test-seata-10093
```

`scaffold-test-auth-10080` 是薄启动模块，依赖 `scaffold-module-rbac-auth-sa-webflux` 提供 `/auth/login`、`/auth/logout`、`/auth/token-info`，Gateway 已配置 `/auth/** -> lb://cloud-auth`。

## 快速启动

1. 安装 Java 21
2. 运行默认业务项目：

```bash
./mvnw -pl scaffold-biz -am spring-boot:run
```

3. 如需本地文件上传或文档功能，参考 `scaffold-biz/src/main/resources/application-local.yml.example` 新建本地 profile 配置

默认策略：

- 文件存储默认关闭
- Swagger 默认关闭
- CORS 默认仅放行本地开发地址
- RBAC 默认不启用，需要显式引入对应业务模块
- 认证 token store 默认是 `memory`，可通过 `security.token.store-type=redis` 切换为 Redis

推荐组合：

- 纯 MVC Web 服务：`scaffold-starter-web` + `scaffold-starter-observability`
- 需要接口文档：再加 `scaffold-starter-swagger`
- 带文件能力：再加 `scaffold-starter-file`
- 带数据库能力：再加 `scaffold-starter-orm`
- 带 Spring Security RBAC：再加 `scaffold-module-rbac-security-servlet`
- 带 Sa-Token RBAC：再加 `scaffold-module-rbac-sa-token-servlet`
- WebFlux Spring Security 认证服务：使用 `scaffold-module-rbac-auth-security-webflux`
- WebFlux Sa-Token 认证服务：使用 `scaffold-module-rbac-auth-sa-webflux`

## 构建说明

默认构建只包含正式交付模块：

```bash
./mvnw clean verify
```

构建普通示例：

```bash
./mvnw -Pexamples clean verify
```

构建 Spring Cloud 示例：

```bash
./mvnw -Pexamples-cloud clean verify
```

只编译认证相关模块：

```bash
./mvnw -Pexamples-cloud \
  -pl :scaffold-module-rbac-security-servlet,:scaffold-module-rbac-sa-token-servlet,:scaffold-test-auth-10080,:scaffold-test-gateway-10000 \
  -am compile
```

## 设计约束

- `scaffold-core-base` 不再直接依赖 `spring-boot-starter-web`，避免 WebFlux 服务被传递引入 Spring MVC
- `scaffold-core-orm` 只保留 ORM 基础实现和公共抽象
- JPA、MyBatis Plus、数据库驱动等接入型依赖由 `scaffold-starter-orm` 暴露
- 认证 starter 只负责技术装配，RBAC 业务接口放在 `scaffold-module`
- Servlet 与 WebFlux starter 不混用，应用按运行时选择依赖
- Spring Security 与 Sa-Token starter 不混用，应用按认证技术栈选择依赖

## 质量保障

- 默认 CI 校验正式交付链路
- `examples` 和 `examples-cloud` profile 用于示例模块冒烟验证
- 认证 starter 拆分后，应重点检查依赖树，避免意外同时引入 `spring-webmvc` 与 `spring-webflux`

## 维护文档

- [贡献约束](CONTRIBUTING.md)
- [模块分层与设计约束](docs/architecture.md)
- [Cloud 示例](scaffold-test-cloud/README.md)
- [RBAC 模块](scaffold-module/scaffold-module-rbac-security-servlet/readme.md)

## 许可证

[MIT License](LICENSE)
