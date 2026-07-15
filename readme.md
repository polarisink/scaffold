# Scaffold - Spring Boot + Vue 管理系统脚手架

面向小团队的前后端分离脚手架。后端采用 Spring Boot 多模块架构，管理端基于 Vue Vben Admin，目标是提供一套默认可运行、按需装配、可持续演进的项目基础盘。

## 当前技术栈

- **Java**: 21
- **Spring Boot**: 3.5.15
- **Spring Cloud**: 2025.0.0
- **Spring Cloud Alibaba**: 2025.0.0.0
- **MyBatis Plus**: 3.5.12
- **MySQL**: 8.0.28
- **Redis**: Redisson
- **认证**: Spring Security / Sa-Token，按技术栈独立装配
- **接口文档**: Knife4j OpenAPI3
- **文件存储** 本地存储（便于快速开发）、s3存储（通用标准）
- **管理端**: Vue 3 + Vite + TypeScript + Naive UI
- **前端包管理器**: pnpm 11

## 模块分层

```text
scaffold/
├── scaffold-biz/              # 默认业务启动项目
├── scaffold-core/             # 基础能力模块
├── scaffold-starters/         # 技术 starter 装配层
├── scaffold-module/           # 可选业务模块，例如 RBAC
├── scaffold-test/             # 普通示例，仅 examples profile 构建
├── scaffold-test-cloud/       # Spring Cloud 示例，仅 examples-cloud profile 构建
└── vue-vben-admin/            # Vue Vben Admin 管理端，当前使用 web-naive
```

分层约束：

- `core` 负责底层能力，不直接表达业务语义
- `starter` 负责技术栈自动装配，不承载 RBAC 这类业务接口
- `module` 负责可选业务能力，引入即可使用，移除不应影响默认启动
- `scaffold-test` 和 `scaffold-test-cloud` 不进入默认构建链路

## Starter 结构

认证 starter 按认证技术栈拆分，避免一个 starter 同时带入 Spring Security、Sa-Token 多套运行时。具体说明参考[starters说明](scaffold-starters/README.md)

```text
scaffold-starters/
├── scaffold-spring-security-starter    # spring-security的starter
├── scaffold-sa-token-starter           # sa-token的starter
├── scaffold-web-starter                # web服务引用此依赖
├── scaffold-openapi-starter            # openapi && knife4j的starter
├── scaffold-orm-starter                # 提供数据库及orm对应的starter（需要自己引入数据库驱动）
├── scaffold-file-starter               # 文件存储（本地及s3两种）starter
├── scaffold-geo-starter                # 地形资源相关starter
├── scaffold-cache-starter              # 缓存（caffeine、redis、postgresql类型）starter
├── scaffold-sse-starter                # sse进行服务端推送的starter
```

认证选择：

| 场景                                       | 推荐依赖                              |
|------------------------------------------|-----------------------------------|
| Spring Boot MVC + Spring Security        | `scaffold-spring-security-starter` |
| Spring Boot MVC + Sa-Token               | `scaffold-sa-token-starter`       |

`scaffold-core-auth` 提供公共认证基础能力，包括：

- `SecurityProperties`
- JWT 工具和 payload 模型
- `TokenStore`
- 基于 Spring Cache 的 token store
- 默认 `PasswordEncoder`
- 通用认证错误码

## RBAC 模块

RBAC 已拆成公共数据库层和两套认证实现：

```text
scaffold-module/scaffold-module-rbac/
├── scaffold-module-rbac-data          # entity、mapper、数据库登录校验服务
├── scaffold-module-rbac-security      # Spring Security Servlet 完整 RBAC
└── scaffold-module-rbac-sa-token      # Sa-Token Servlet 完整 RBAC
```

选择方式：

- 使用 Spring Security 体系：引入 `scaffold-module-rbac-security`
- 使用 Sa-Token 体系：引入 `scaffold-module-rbac-sa-token`
- 只复用用户、角色、菜单数据库访问：引入 `scaffold-module-rbac-data`

所有 RBAC 认证模块都复用 `scaffold-module-rbac-data`，避免数据库层重复实现。Servlet 版本提供完整后台管理接口，包含 `/auth`、
`/user`、`/role`、`/menu`、`/org`。

## Cloud 示例

`scaffold-test-cloud` 提供 Spring Cloud、Gateway、Nacos、Dubbo、Seata、Sentinel 示例，并提供基于 Web MVC 的认证服务：

```text
scaffold-test-cloud/
├── scaffold-test-auth-10080      # Sa-Token Web MVC 认证服务
├── scaffold-test-gateway-10000   # Spring Cloud Gateway WebFlux
├── scaffold-test-provider-10081
├── scaffold-test-consumer-10082
├── scaffold-test-order-10083
├── scaffold-test-dubbo-*
└── scaffold-cloud-common-dependencies
```

`scaffold-test-auth-10080` 依赖 `scaffold-module-rbac-sa-token` 提供 `/auth/login`、`/auth/logout`，
Gateway 已配置 `/auth/** -> lb://cloud-auth`。

## 快速启动

### 后端

环境要求：Java 21、Maven 3.9+（仓库已提供 Maven Wrapper）。

运行默认业务项目：

```bash
./mvnw -pl scaffold-biz -am spring-boot:run
```

服务默认监听 `http://localhost:8082`，使用文件型 H2 数据库，首次运行无需预先安装 MySQL 或 Redis。

### 管理端

环境要求：Node.js 22.18+（或 24.x）、pnpm 11+。

```bash
cd vue-vben-admin
pnpm install
pnpm dev:naive
```

开发服务器默认监听 `http://localhost:5888`，`/api` 请求会代理到 `http://localhost:8082`。建议先启动后端，再启动管理端。

默认策略：

- 文件存储默认使用项目目录下的 `./www`
- Swagger 默认开启
- CORS 默认仅放行本地开发地址
- 默认业务项目启用 Sa-Token RBAC；可在 `scaffold-biz/pom.xml` 中替换为 Spring Security 实现，或移除 RBAC 模块
- 认证 token store 使用 Spring Cache，缓存后端通过 `spring.cache.type` 选择；Caffeine 下 `security_token` 可通过 `scaffold.security.token.cache-ttl` 单独配置过期时间
- Spring Security JWT 密钥由 `scaffold.security.token.jwt-secret` 配置，建议通过至少 32 字节的 `SCAFFOLD_JWT_SECRET` 环境变量提供

推荐组合：

- 纯 MVC Web 服务：`scaffold-web-starter`
- 需要接口文档：再加 `scaffold-openapi-starter`
- 带文件能力：再加 `scaffold-file-starter`
- 带数据库能力：再加 `scaffold-orm-starter`
- 带 Spring Security RBAC：再加 `scaffold-module-rbac-security`
- 带 Sa-Token RBAC：再加 `scaffold-module-rbac-sa-token`

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
  -pl :scaffold-module-rbac-security,:scaffold-module-rbac-sa-token,:scaffold-test-auth-10080,:scaffold-test-gateway-10000 \
  -am compile
```

## 设计约束

- `scaffold-core-base` 不再直接依赖 `spring-boot-starter-web`，避免 WebFlux 服务被传递引入 Spring MVC
- `scaffold-core-orm` 只保留 ORM 基础实现和公共抽象
- JPA、MyBatis Plus、数据库驱动等接入型依赖由 `scaffold-orm-starter` 暴露
- 认证 starter 只负责技术装配，RBAC 业务接口放在 `scaffold-module`
- Servlet 应用与 WebFlux 应用应隔离 Web 运行时依赖
- Spring Security 与 Sa-Token starter 不混用，应用按认证技术栈选择依赖

## 质量保障

- 默认 CI 校验正式交付链路
- `examples` 和 `examples-cloud` profile 用于示例模块验证
- 认证 starter 拆分后，应重点检查依赖树，避免意外同时引入 `spring-webmvc` 与 `spring-webflux`

## 维护文档

- [贡献约束](CONTRIBUTING.md)
- [模块分层与设计约束](docs/architecture.md)
- [Core 模块索引](scaffold-core/README.md)
- [Starter 选择与接入](scaffold-starters/README.md)
- [Cloud 示例](scaffold-test-cloud/README.md)
- [RBAC（Spring Security）模块](scaffold-module/scaffold-module-rbac/scaffold-module-rbac-security/readme.md)
- [管理端](vue-vben-admin/README.zh-CN.md)

## 许可证

MIT License
