# Scaffold - Spring Boot + Vue 管理系统脚手架

面向小团队的前后端分离脚手架。后端采用 Spring Boot 多模块架构，管理端基于 Vue Vben Admin，目标是提供一套默认可运行、按需装配、可持续演进的项目基础盘。

## 技术栈

- **后端**：Java 21、Spring Boot 3.5.15、Spring Cloud 2025.0.0、Spring Cloud Alibaba 2025.0.0.0
- **数据访问**：MyBatis Plus 3.5.12、JPA、MySQL 8、H2
- **缓存与消息**：Caffeine、Redisson、Redis Stream、Redis Pub/Sub
- **AI 能力**：Spring AI、同步/流式聊天、会话记忆、Tool Calling
- **认证与权限**：Spring Security、Sa-Token、可选 RBAC 模块
- **接口与存储**：Knife4j OpenAPI 3、本地文件、S3
- **管理端**：Vue 3、Vite、TypeScript、Naive UI、pnpm 11

## 项目结构

```text
scaffold/
├── scaffold-biz/              # 默认可运行的业务应用
├── scaffold-core/             # 无业务语义的基础实现与公共抽象
├── scaffold-starters/         # 第三方技术栈与 Spring Boot 自动装配
├── scaffold-module/           # 可选业务模块，例如 RBAC、代码生成
├── scaffold-test/             # 普通示例，仅 examples profile 构建
├── scaffold-test-cloud/       # Cloud 示例，仅 examples-cloud profile 构建
└── vue-vben-admin/            # 管理端，当前使用 web-naive
```

依赖方向保持为“应用 → module → starter → core”：

- `core` 提供底层实现与公共抽象，不表达业务语义
- `starter` 组合 core 与第三方依赖，负责配置属性、条件 Bean 和自动装配
- `module` 提供数据模型、业务用例、接口与权限语义，可以按需引入或移除
- `scaffold-biz` 选择并组合具体 starter 和 module，不依赖聚合 POM

完整边界和演进原则见[模块分层与设计约束](docs/architecture.md)。

## 快速启动

### 后端

环境要求：Java 21、Maven 3.9+。仓库已提供 Maven Wrapper。

```bash
./mvnw -pl scaffold-biz -am spring-boot:run
```

服务默认监听 `http://localhost:8082`。默认使用文件型 H2 数据库，首次运行不需要预先安装 MySQL 或 Redis。

#### 原生镜像

安装 GraalVM 21 和 `native-image` 后，可直接生成当前平台的原生可执行文件：

```bash
./mvnw -Pnative -pl scaffold-biz -am -DskipTests package
./scaffold-biz/target/scaffold-biz
```

也可以只依赖 Docker，生成 Linux 原生 OCI 镜像：

```bash
docker build -f scaffold-biz/Dockerfile.native -t scaffold-biz:native .
docker run --rm -p 8082:8082 scaffold-biz:native
```

### 管理端

环境要求：Node.js 22.18+（或 24.x）、pnpm 11+。

```bash
cd vue-vben-admin
pnpm install
pnpm dev:naive
```

开发服务器默认监听 `http://localhost:5888`，并将 `/api` 代理到 `http://localhost:8082`。

### 默认应用策略

`scaffold-biz/application.yml` 显式选择适合本地演示的配置：

- 本地文件存储目录为 `${user.home}/scaffold/storage`
- Swagger 开启，CORS 仅放行本地开发地址
- 默认启用 Sa-Token RBAC；可以替换为 Spring Security 实现或移除 RBAC
- token store 使用 Spring Cache，缓存后端由 `spring.cache.type` 选择
- Spring Security JWT 密钥由 `scaffold.security.token.jwt-secret` 配置，生产环境建议通过至少 32 字节的 `SCAFFOLD_JWT_SECRET` 环境变量提供

Starter 自身仍采用保守默认值，例如文件存储和 Swagger 默认关闭，只有最终应用显式配置后才启用。

## 功能模块

### RBAC

```text
scaffold-module/scaffold-module-rbac/
├── scaffold-module-rbac-data          # 数据模型、公共管理用例与登录校验
├── scaffold-module-rbac-security      # Spring Security Servlet 实现
└── scaffold-module-rbac-sa-token      # Sa-Token Servlet 实现
```

- 使用 Spring Security：引入 `scaffold-module-rbac-security`
- 使用 Sa-Token：引入 `scaffold-module-rbac-sa-token`
- 只复用用户、角色和菜单数据访问：引入 `scaffold-module-rbac-data`

两个认证实现复用唯一的数据管理逻辑，认证模块只适配当前用户和会话失效机制。管理接口包括 `/auth`、`/user`、`/role`、`/menu` 和 `/org`。

### 代码生成

`scaffold-module-codegen` 是可选的轻量代码生成器。它从当前数据源读取表、字段、主键和唯一索引，保存生成配置，并通过 FreeMarker 生成 ZIP：

- 后端 Entity、DTO、MyBatis Plus Mapper、Service 和 Controller
- 前端 API、TypeScript 类型和 Naive UI、Element Plus、Ant Design Vue 页面模板
- 只生成 ZIP，不直接写入或覆盖工作区文件

默认 `scaffold-biz` 不启用该模块。依赖方式、接口、默认路径和生成约束见[代码生成模块说明](scaffold-module/scaffold-module-codegen/README.md)。

### Starter 能力

| Starter | 能力 |
| --- | --- |
| `scaffold-ai-starter` | Spring AI 同步/流式聊天、会话记忆与 Tool Calling |
| `scaffold-web-starter` | Spring MVC、统一响应、异常处理、链路日志 |
| `scaffold-orm-starter` | JPA、MyBatis Plus；数据库驱动由应用选择 |
| `scaffold-security-starter` | Spring Security 无状态认证 |
| `scaffold-sa-token-starter` | Sa-Token MVC 认证 |
| `scaffold-openapi-starter` | Knife4j OpenAPI 3 |
| `scaffold-file-starter` | 本地与 S3 文件存储 |
| `scaffold-cache-starter` | Caffeine、Redis、PostgreSQL 单级/二级缓存 |
| `scaffold-redis-messaging-starter` | Redis Stream 与 Pub/Sub |
| `scaffold-geo-starter` | DEM 高程与地形分析 |
| `scaffold-socketio-starter` | Netty Socket.IO 服务端 |
| `scaffold-sse-starter` | SSE 服务端推送 |

配置前缀、依赖组合和接入示例见 [Starter 选择与接入](scaffold-starters/README.md)。Spring Security 与 Sa-Token Starter 不应同时引入。

## 构建与测试

```bash
# 正式交付模块
./mvnw clean verify

# 普通示例
./mvnw -Pexamples clean verify

# Spring Cloud 示例
./mvnw -Pexamples-cloud clean verify
```

`scaffold-test` 和 `scaffold-test-cloud` 不进入默认构建链路，分别由 `examples` 和 `examples-cloud` profile 验证。

## 关键设计原则

- `scaffold-core-base` 不传递引入 Spring MVC 运行时；Servlet 与 WebFlux 应用隔离依赖
- 接入型依赖由 starter 暴露，业务接口只放在 module
- 应用依赖具体 module，starter 不反向依赖 module
- 可选 module 移除后不应破坏应用的基础启动链路
- 认证技术栈单选，避免同时装入 Spring Security 与 Sa-Token
- 新增基础能力时同步提供自动装配测试和接入文档

详细约束统一维护在 [docs/architecture.md](docs/architecture.md)。

## 已知兼容性限制

`netty-socketio 2.0.14` 目前要求项目 BOM 将 Netty 全局固定为 `4.1.130.Final`。升级或引入 Gateway、WebFlux、Vert.x、Dubbo、Redisson 等 Netty 组件时必须验证依赖兼容性；长期应将该覆盖隔离到 Socket.IO 专用应用或 BOM。详见 [Socket.IO Starter](scaffold-starters/scaffold-socketio-starter/README.md)。

## 文档索引

- [贡献约束](CONTRIBUTING.md)
- [模块分层与设计约束](docs/architecture.md)
- [项目路线图](docs/roadmap.md)
- [Core 模块索引](scaffold-core/README.md)
- [Starter 选择与接入](scaffold-starters/README.md)
- [AI Starter](scaffold-starters/scaffold-ai-starter/README.md)
- [代码生成模块](scaffold-module/scaffold-module-codegen/README.md)
- [RBAC（Spring Security）模块](scaffold-module/scaffold-module-rbac/scaffold-module-rbac-security/readme.md)
- [Cloud 示例](scaffold-test-cloud/README.md)
- [管理端](vue-vben-admin/README.zh-CN.md)

## 许可证

MIT License
