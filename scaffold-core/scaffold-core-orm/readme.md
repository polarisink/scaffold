# scaffold-core-orm

ORM 底层公共实现，提供 MyBatis Plus 基础设施和持久化审计模型。

## 主要能力

- `BaseAuditable`：审计字段实体基类。
- `DefaultMetaObjectHandler`：MyBatis Plus 审计字段自动填充。
- `MyBaseMapper`：项目通用 Mapper 基类。
- `MysqlInjector`：自定义 SQL 注入扩展。

Spring Boot 业务应用应使用 [`scaffold-orm-starter`](../../scaffold-starters/scaffold-orm-starter/README.md)，MyBatis Plus 的插件与全局 Bean 默认值由 Starter 注册。本模块适合框架扩展或只复用底层类型的场景。

```xml
<dependency>
    <groupId>com.scaffold</groupId>
    <artifactId>scaffold-core-orm</artifactId>
</dependency>
```

数据库驱动仍需由最终应用根据实际数据库显式提供。
