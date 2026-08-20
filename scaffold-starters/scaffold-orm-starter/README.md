# scaffold-orm-starter

JPA 与 MyBatis Plus 的统一接入 Starter，同时传递引入 `scaffold-core-orm`。

## 主要能力

- Spring Data JPA 与 MyBatis Plus 运行时依赖。
- 注册 MyBatis Plus 分页插件、全局配置、SQL Injector 等默认 Bean，均允许应用自定义 Bean 覆盖。
- 没有自定义 `MetaObjectHandler` 时注册 `DefaultMetaObjectHandler`。
- `PageUtils` 将 JPA `Page` 或 MyBatis Plus `IPage` 转换为统一 `PageResponse`。

## 接入

```xml
<dependency>
    <groupId>com.scaffold</groupId>
    <artifactId>scaffold-orm-starter</artifactId>
</dependency>
```

应用仍需提供数据库驱动和数据源配置，例如：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/scaffold
    username: root
    password: ${DB_PASSWORD}
```

## MyBatis Plus 分页配置

Starter 默认注册 `MybatisPlusInterceptor`，并启用 MyBatis Plus 的分页插件。可通过 `scaffold.orm.pagination` 调整分页行为：

```yaml
scaffold:
  orm:
    pagination:
      enabled: true
      db-type: mysql
      overflow: false
      max-limit: 500
```

| 配置项 | 默认值 | 说明 |
| --- | --- | --- |
| `enabled` | `true` | 是否向 `MybatisPlusInterceptor` 添加分页插件；设为 `false` 时不处理 MyBatis Plus 分页 SQL。 |
| `db-type` | 无 | 数据库类型，例如 `mysql`、`postgresql`。未配置时由 MyBatis Plus 根据当前数据源动态识别，使用多数据源时建议保持不配置。 |
| `overflow` | `false` | 请求页码超过总页数时是否回到第一页；关闭时返回空记录。 |
| `max-limit` | `500` | 单页允许返回的最大记录数，用于限制过大的分页请求。 |

仅需修改部分行为时可以省略其他配置，例如取消单页数量限制：

```yaml
scaffold:
  orm:
    pagination:
      max-limit: -1
```

如果只使用 JPA 或只使用 MyBatis Plus，可通过 Maven exclusions 缩小依赖；自定义审计填充时声明自己的 `MetaObjectHandler` Bean 即可覆盖默认实现。
