# scaffold-orm-starter

JPA 与 MyBatis Plus 的统一接入 Starter，同时传递引入 `scaffold-core-orm`。

## 主要能力

- Spring Data JPA 与 MyBatis Plus 运行时依赖。
- 自动导入 `MyBatisPlusConfig`。
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

如果只使用 JPA 或只使用 MyBatis Plus，可通过 Maven exclusions 缩小依赖；自定义审计填充时声明自己的 `MetaObjectHandler` Bean 即可覆盖默认实现。
