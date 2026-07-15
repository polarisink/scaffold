# SQLite RCS 数据示例

演示对任意 SQLite 文件按路径动态创建和复用 `JdbcTemplate`，读写 `data_tb` 中的雷达散射截面（RCS）数据，并将 BLOB 按小端序解析为 `float[361][181]`。

核心方法：

- `selectByPath`：读取完整记录和 RCS BLOB
- `selectSummaryByPath`：不加载大字段的摘要查询
- `addOrUpdate`：自动建库建表并按 ID upsert
- `deleteByPath`：必须带至少一个条件，防止误删全表

该模块没有 REST 接口。建议通过测试验证：

```bash
./mvnw -pl scaffold-test/scaffold-test-sqlite -am -Pexamples test
```

数据库文件路径会规范化并按文件缓存 Hikari 数据源，Spring 容器关闭时统一释放。完整 RCS BLOB 固定需要 `361 × 181 × 4` 字节；尺寸不符会被拒绝。
