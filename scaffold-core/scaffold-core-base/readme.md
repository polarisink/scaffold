# scaffold-core-base

项目最基础的公共类型和工具集合。该模块只依赖 Spring 的细粒度组件，不直接引入 `spring-boot-starter-web`。

## 主要能力

- `R`：统一 API 返回模型。
- `BaseException`、`IResponseEnum`、`Assert`：业务异常与断言。
- `PageRequest`、`PageResponse`：通用分页模型。
- `ITree`、`TreeIterators`：树结构抽象与遍历。
- `JsonUtil`、`JacksonConfig`：JSON 工具和 Jackson 配置。
- 枚举转换、时间、集合、Servlet、异步任务和 Caffeine 辅助能力。

业务 Web 项目优先依赖 [`scaffold-web-starter`](../../scaffold-starters/scaffold-web-starter/README.md)。非 Web 模块需要公共模型时可直接依赖：

```xml
<dependency>
    <groupId>com.scaffold</groupId>
    <artifactId>scaffold-core-base</artifactId>
</dependency>
```

本模块不应加入数据库、认证或具体业务逻辑。
