# scaffold-core

`scaffold-core` 保存可复用的底层实现与公共抽象。业务应用通常应优先依赖
[`scaffold-starters`](../scaffold-starters/README.md)，只有扩展框架或需要直接使用底层 API 时才直接依赖 Core 模块。

## 模块

| 模块 | 职责 | 推荐入口 |
| --- | --- | --- |
| [`scaffold-core-base`](scaffold-core-base/README.md) | 返回模型、异常、JSON、分页、树结构等基础能力 | `scaffold-web-starter` |
| [`scaffold-core-auth`](scaffold-core-auth/README.md) | JWT、TokenStore 和认证公共配置 | 两种认证 Starter |
| [`scaffold-core-orm`](scaffold-core-orm/README.md) | MyBatis Plus 与审计实体基础实现 | `scaffold-orm-starter` |
| [`scaffold-core-redis`](scaffold-core-redis/README.md) | Redisson、Redis Stream、Pub/Sub 与工具类 | 按需直接引入 |
| [`scaffold-core-postgresql-cache`](scaffold-core-postgresql-cache/README.md) | PostgreSQL Spring Cache 实现 | `scaffold-postgresql-cache-starter` |
| [`scaffold-core-websocket`](scaffold-core-websocket/README.md) | Netty Socket.IO 服务端基础设施 | 按需直接引入 |
| [`scaffold-core-geo`](scaffold-core-geo/README.md) | 地理计算、DEM 高程与地形分析 | `scaffold-geo-starter` |

## 开发约束

- Core 不表达用户、角色、菜单等业务语义。
- Core 不负责“引入即用”的完整装配；自动配置优先放入 Starter。
- 避免在基础模块中传递引入不必要的 Web 运行时。
- 新增公共 API 时应保持小而稳定，并在对应模块 README 中说明边界。

版本由根项目和 `scaffold-dependencies` 统一管理，模块间依赖不要单独声明版本。
