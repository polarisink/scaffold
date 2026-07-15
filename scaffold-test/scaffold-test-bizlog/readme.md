# BizLog 操作日志示例

演示 `bizlog-sdk` 的 `@LogRecord` 注解、SpEL 变量、操作人获取和自定义日志落库服务。示例业务只打印/模拟数据，不连接真实用户数据库。

```bash
./mvnw -pl scaffold-test/scaffold-test-bizlog -am -Pexamples spring-boot:run
```

更新用户并记录修改前后值：

```bash
curl -X POST http://localhost:8888/user/update \
  -H 'Content-Type: application/json' \
  -d '{"userId":1,"username":"新名字","phone":"13900000000","age":21}'
```

删除日志：

```bash
curl http://localhost:8888/user/delete/1
```

重点代码：`UserService` 定义日志模板并通过 `LogRecordContext` 注入旧值；`CustomOperatorGetService` 提供操作人；`CustomLogRecordService` 接收最终日志。生产使用时需替换模拟业务与日志存储。
