# Scaffold - Spring Boot 脚手架

一个现代化的 Spring Boot 脚手架项目，集成多种常用功能模块，旨在快速构建企业级应用。

## 技术栈

- **Spring Boot**: 3.2.4
- **MyBatis Plus**: 3.5.12
- **数据库**: MySQL 8.0.23
- **Redis**: 通过 Redisson 操作
- **文件存储**: 支持本地和 S3/MinIO 存储
- **接口文档**: Knife4j OpenAPI3
- **工具库**: Hutool、Guava、Apache Commons Lang3
- **JNA**: 用于调用本地库
- **WebSocket**: 实时通信支持
- **SSE**: Server-Sent Events 支持

## 项目结构

```
scaffold/
├── scaffold-biz/          # 业务模块
├── scaffold-core/         # 核心模块
│   ├── scaffold-core-base # 基础功能
│   ├── scaffold-core-file # 文件存储
│   ├── scaffold-core-log  # 日志处理
│   ├── scaffold-core-orm  # ORM 框架
│   ├── scaffold-core-redis # Redis 操作
│   ├── scaffold-core-security # 安全框架
│   ├── scaffold-core-swagger # 接口文档
│   └── scaffold-core-websocket # WebSocket 支持
├── scaffold-module/       # 业务模块
│   └── scaffold-module-rbac # RBAC 权限管理
├── scaffold-starters/     # 启动器
│   └── scaffold-starter-web # Web 启动器
└── scaffold-test/         # 测试模块
    ├── scaffold-test-download # 下载功能测试
    ├── scaffold-test-http-interface # HTTP 接口测试
    ├── scaffold-test-redis # Redis 测试
    ├── scaffold-test-sse # SSE 测试
    ├── scaffold-test-udp # UDP 测试
    └── scaffold-test-websocket # WebSocket 测试
```

## 功能特性

- **统一响应格式**: R类提供统一的响应格式
- **异常处理**: 全局异常处理机制
- **配置管理**: 统一的配置管理
- **文件上传下载**: 支持本地和云存储
- **日志管理**: 基于注解的日志记录
- **安全框架**: JWT 认证和授权
- **异步任务**: 异步任务支持
- **缓存**: Redis 缓存支持
- **分页**: MyBatis Plus 分页支持

## 许可证

[MIT License](LICENSE)