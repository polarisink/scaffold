# scaffold-module-codegen

基于数据库元数据和 FreeMarker 的轻量代码生成模块。应用显式引入后，可以维护表级和字段级生成配置，并将完整源码作为 ZIP 下载。

## 接入方式

模块依赖 Web 和 ORM Starter，需要应用提供可用的 `DataSource`：

```xml
<dependency>
    <groupId>com.scaffold</groupId>
    <artifactId>scaffold-module-codegen</artifactId>
</dependency>
```

该模块是可选业务能力，默认 `scaffold-biz` 不会自动启用。

## 工作流程

1. 通过 JDBC `DatabaseMetaData` 查询当前数据源中的表。
2. 导入表名、字段、主键、单列唯一索引和数据库类型。
3. 调整包名、模块名、字段类型、查询方式、表单控件和权限前缀等配置。
4. 使用 FreeMarker 渲染源码并下载 ZIP。
5. 人工检查生成结果后合入业务项目。

生成器不会直接写入或覆盖工作区文件，也不会自动执行建表、菜单写入或代码编译。

## 接口

所有接口以 `/codegen` 为前缀：

| 方法 | 路径 | 用途 |
| --- | --- | --- |
| `GET` | `/configs` | 查询生成配置 |
| `GET` | `/configs/{id}` | 查询配置及字段 |
| `POST` | `/configs` | 新建生成配置 |
| `PUT` | `/configs/{id}` | 更新生成配置 |
| `DELETE` | `/configs/{id}` | 逻辑删除生成配置 |
| `GET` | `/database/tables` | 查询当前数据源的表 |
| `POST` | `/database/import` | 从数据库导入指定表 |
| `GET` | `/configs/{id}/download` | 下载生成的 ZIP |

`/tables` 是 `/configs` 的兼容别名。

## 生成内容

后端默认生成：

- Entity
- 查询 DTO 和新增 DTO
- MyBatis Plus Mapper
- Service
- Controller

前端默认生成：

- API 请求文件
- TypeScript 类型文件
- Naive UI 页面 `index.vue`
- Element Plus 页面 `index.ele.vue`
- Ant Design Vue 页面 `index.antd.vue`

默认路径：

- 后端：`scaffold-biz/src/main/java`
- 前端：`vue-vben-admin/apps/web-naive/src`

这些路径会写入 ZIP 条目，不代表生成器会直接修改对应目录。

## 使用约束

- 数据库元数据在不同驱动上的字段备注、schema 和类型表现可能不同，导入后需要复核。
- 生成模板提供 CRUD 基础结构，不替代领域建模、事务边界、数据权限和参数校验设计。
- 合入前检查包名、Java/TypeScript 类型、唯一约束、查询方式、权限标识和前端路由。
- 调整模板时应同步更新 `CodeArchiveGeneratorTest`，确保 ZIP 路径和核心输出仍然有效。
