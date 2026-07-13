# scaffold-starters

Starter 是业务应用的推荐接入层，负责聚合依赖、注册 Spring Boot 自动配置并提供合理默认值。

## 选择指南

| Starter | 用途 | 关键前缀 |
| --- | --- | --- |
| [`scaffold-web-starter`](scaffold-web-starter/README.md) | Spring MVC、统一响应、异常、链路日志 | `scaffold.web` |
| [`scaffold-orm-starter`](scaffold-orm-starter/README.md) | JPA 与 MyBatis Plus | Spring 标准配置 |
| [`scaffold-spring-security-starter`](scaffold-spring-security-starter/README.md) | Spring Security 无状态认证 | `scaffold.security` |
| [`scaffold-sa-token-starter`](scaffold-sa-token-starter/README.md) | Sa-Token MVC 认证 | `scaffold.security`、`sa-token` |
| [`scaffold-openapi-starter`](scaffold-openapi-starter/README.md) | Knife4j OpenAPI 3 文档 | `swagger` |
| [`scaffold-file-starter`](scaffold-file-starter/README.md) | 本地或 S3 文件存储 | `scaffold.file-storage` |
| [`scaffold-cache-starter`](scaffold-cache-starter/README.md) | Caffeine、Redis、PostgreSQL 单级/二级缓存 | `scaffold.cache` |
| [`scaffold-postgresql-job-starter`](scaffold-postgresql-job-starter/README.md) | PostgreSQL 任务队列 | `scaffold.job.postgresql` |
| [`scaffold-geo-starter`](scaffold-geo-starter/README.md) | DEM 高程与地形分析 | `scaffold.geo` |

## 常见组合

- 普通 MVC API：`scaffold-web-starter`
- 数据库 API：Web + ORM
- Spring Security 后台：ORM + `scaffold-module-rbac-security`
- Sa-Token 后台：ORM + `scaffold-module-rbac-sa-token`
- API 文档、文件、地理、缓存和任务队列均按需追加。

Spring Security 与 Sa-Token 两个认证 Starter 不应同时引入。数据库驱动、Redis 服务、S3 服务和 DEM 文件等外部资源仍由最终应用选择并配置。

所有项目模块版本由 `scaffold-dependencies` 管理，使用同一父项目时不需要在依赖中填写 `<version>`。
