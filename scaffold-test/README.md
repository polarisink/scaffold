# scaffold-test 示例模块

本目录集中存放框架能力验证、第三方组件集成和协议实验代码。多数 Spring Boot 示例需要启用 Maven 的 `examples` Profile：

```bash
./mvnw -pl scaffold-test/<模块名> -am -Pexamples spring-boot:run
```

只运行测试：

```bash
./mvnw -pl scaffold-test/<模块名> -am -Pexamples test
```

## 模块索引

| 模块 | 用途 | 默认端口/运行形态 |
| --- | --- | --- |
| `scaffold-test-audio` | Whisper 音频转写、会议记录处理骨架 | 代码骨架 |
| `scaffold-test-bizlog` | `bizlog-sdk` 操作日志 | `8888` |
| `scaffold-test-cache` | PostgreSQL 缓存实现 | `8096` |
| `scaffold-test-download` | 注解式文件下载 | `8083` |
| `scaffold-test-flink` | Flink DataStream WordCount | Flink 作业 |
| `scaffold-test-geo` | Geo Starter 依赖验证 | 依赖骨架 |
| `scaffold-test-http-interface` | Retrofit、RestClient、WebClient 对比 | `8888` |
| `scaffold-test-langchain4j` | LangChain4j Starter 最小启动工程 | `8082` |
| `scaffold-test-netty-socketio` | Socket.IO 与 Redis 消息 | Web `8081` |
| `scaffold-test-playwright` | HTML 转 PDF | `8080` |
| `scaffold-test-postgresql-geo` | PostgreSQL/PostGIS 空间查询 | `8096` |
| `scaffold-test-qwen3-asr` | Qwen3-ASR 离线语音识别 | `127.0.0.1:8093` |
| `scaffold-test-redis` | Redis Stream 收发 | `8080` |
| `scaffold-test-sa-token` | Sa-Token 登录流程 | `8081` |
| `scaffold-test-sb-admin-client` | Spring Boot Admin 客户端 | `9999` |
| `scaffold-test-sb-admin-server` | Spring Boot Admin 服务端 | `10000` |
| `scaffold-test-spi` | Java SPI 插件机制 | 独立 `main` |
| `scaffold-test-sqlite` | 动态 SQLite 文件与 RCS BLOB | 测试/服务类 |
| `scaffold-test-sse` | SSE 用户与房间推送 | `8100` |
| `scaffold-test-udp` | Netty UDP 与 JNA/PDW | Web `8082` |
| `scaffold-test-vertx` | 原生 Vert.x 多协议服务 | `9076`–`9080` |
| `scaffold-test-vertx-spring` | Spring 管理 Vert.x Verticle | 见模块配置 |
| `scaffold-test-yauaa` | User-Agent 解析 | `8018` |

各模块的环境依赖、接口示例和已知限制见其目录内 README。示例配置中的账号、密码和本机路径仅用于本地验证，使用前请按环境覆盖，勿直接用于生产。
