# Scaffold - Spring Boot 脚手架

面向小团队的 Spring Boot 多模块脚手架，目标是提供一个默认精简、按需装配、可以持续演进的后端基础盘。

## 当前基线

- **Spring Boot**: 3.5.7
- **Java**: 21
- **MyBatis Plus**: 3.5.12
- **MySQL**: 8.0.23
- **Redis**: Redisson
- **文件存储**: 本地 / S3
- **接口文档**: Knife4j OpenAPI3

## 模块分层

```text
scaffold/
├── scaffold-biz/           # 默认业务启动项目
├── scaffold-core/          # 基础能力模块
├── scaffold-starters/      # 面向业务的 starter 装配层
├── scaffold-module/        # 可选业务模块，例如 rbac
└── scaffold-test/          # 技术实验 / 示例模块，仅 examples profile 构建
```

分层约束：

- `core` 负责底层能力，不直接表达业务语义
- `starter` 负责组合装配，对业务项目暴露最少接入面
- `module` 负责可选业务能力，引入即可生效，移除不应影响默认启动
- `scaffold-test` 不进入默认构建链路

当前 starter 结构：

- `scaffold-starter-web`: Web 基座、统一返回、参数校验
- `scaffold-starter-swagger`: OpenAPI / Knife4j 文档自动装配
- `scaffold-starter-security`: 安全认证、默认 token 存储、Redis token 支持
- `scaffold-starter-orm`: JPA、MyBatis Plus、数据库驱动和 ORM 接入
- `scaffold-starter-file`: 文件上传、访问映射和存储接入
- `scaffold-starter-observability`: 日志切面与 Actuator

设计约束：

- `scaffold-core-orm` 只保留 ORM 基础实现和公共抽象
- JPA、MyBatis Plus、数据库驱动等接入型依赖统一由 `scaffold-starter-orm` 暴露给业务模块
- `scaffold-starter-security` 默认提供内存 token 存储，也可通过配置切换到 Redis
- `scaffold-starter-file` 负责暴露文件上传服务、访问路径映射和上传接口
- `scaffold-starter-swagger` 独立暴露接口文档自动装配，避免 `starter-web` 默认耦合文档依赖

## 快速启动

1. 安装 Java 21 和 Maven 3.9+
2. 运行默认业务项目：

```bash
mvn -pl scaffold-biz -am spring-boot:run
```

3. 如果需要本地文件上传或文档功能，参考 [application-local.yml.example](/Users/aries/IdeaProjects/scaffold/scaffold-biz/src/main/resources/application-local.yml.example) 新建本地 profile 配置

默认策略：

- 文件存储默认关闭
- Swagger 默认关闭
- CORS 默认仅放行本地开发地址
- `rbac` 默认不启用，需要显式引入模块依赖
- `security.token.store-type` 默认是 `memory`，可切换为 `redis`

推荐组合：

- 纯 Web 服务：`scaffold-starter-web` + `scaffold-starter-observability`
- 需要接口文档：再加 `scaffold-starter-swagger`
- 带文件能力：再加 `scaffold-starter-file`
- 带数据库能力：再加 `scaffold-starter-orm`
- 带认证能力：再加 `scaffold-starter-security`

## 构建说明

默认构建只包含正式交付模块：

```bash
mvn clean verify
```

如需构建示例和实验模块：

```bash
mvn -Pexamples clean verify
```

## 改造重点

- 移除了 `scaffold-biz` 对 `rbac` 的硬编码扫描
- `scaffold-test` 已移入 `examples` profile
- `starter-web` 不再向业务项目传播测试依赖
- 文档和文件存储改为默认关闭，避免把 demo 配置带到生产

## 许可证

[MIT License](LICENSE)
