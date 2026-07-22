# scaffold-test-ai

`ai-starter` 的可运行示例与集成测试。应用默认接入 DeepSeek，启动前需要设置
`DEEPSEEK_API_KEY`。

本模块将持续演进为一个单一的 **AI 智能售后工单系统（Scaffold AI Support）**，用同一条业务链路
循序学习结构化输出、Prompt、Tool Calling、权限、RAG、人工确认和 AI 评测。各阶段目标、目录规划
与完成标准见 [AI 智能售后工单系统建设路线](docs/ai-support-roadmap.md)。

```bash
./mvnw -Pexamples -pl scaffold-test/scaffold-test-ai -am install -DskipTests
./mvnw -Pexamples -pl scaffold-test/scaffold-test-ai spring-boot:run
```

前端已迁移到模块内的独立 Vue 3 工程 `frontend`。后端启动后，再开启一个终端运行：

```bash
cd scaffold-test/scaffold-test-ai/frontend
pnpm install
pnpm dev
```

打开 <http://localhost:5173>。开发服务器会将 `/api` 请求代理到 <http://localhost:8101>，无需额外配置
CORS。前端采用 Vue 3、TypeScript、Vite、Pinia、Vue Router 和 Ant Design Vue，工程组织参考
`vue-vben-admin`，但不引入学习示例暂时不需要的后台权限、国际化和微前端基础设施。

后端使用 `scaffold-module-rbac-security` 和 Spring Security 进行无状态 JWT 认证。学习环境首次启动会
初始化默认账号 `admin / admin`。前端路由是静态路由，登录成功后将 Token 保存到浏览器并为 API、SSE
请求自动添加 `Authorization: Bearer <token>`；收到 401 时清理登录状态并返回登录页。生产环境必须修改
默认密码，并通过 `SCAFFOLD_JWT_SECRET` 提供至少 32 字节的随机 JWT 密钥。

页面右上角提供“退出”按钮，调用 `POST /auth/logout` 使服务端 Token 立即失效，并清除浏览器认证信息和
当前用户的前端工单、对话缓存，然后返回登录页以便切换账号。

如需改用 OpenAI，增加 `spring-ai-starter-model-openai` 依赖，将
`spring.ai.model.chat` 改为 `openai`，并配置：

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      # OpenAI 兼容服务可配置 base-url
      base-url: https://api.openai.com
      chat:
        options:
          model: gpt-4o-mini
```

接口文档默认开启：

- Knife4j：<http://localhost:8101/doc.html>
- OpenAPI JSON：<http://localhost:8101/v3/api-docs>

## 版本化 Prompt 与结构化输出示例

阶段一的工单意图识别示例将 Prompt 与 Java 代码分离：

```text
src/main/resources/prompts/support/intent/v1/
├── system.st
└── user.st
```

调用接口：

```http
POST /api/examples/support/intents/analyze
Content-Type: application/json

{
  "conversationId": "support-42",
  "message": "手机无法开机，订单号202607190001，我要申请退款"
}
```

响应会被转换并校验为 `WorkOrderIntent`，而不是返回自由文本。`system.st` 保存稳定的角色与业务规则，
`user.st` 仅描述本次任务并通过 `{message}` 接收变量。升级 Prompt 时新增 `v2` 目录并保留回归测试，
不要直接覆盖线上版本。打开 Vue 前端即可使用“阶段一 · 工单意图分析”表单体验该流程。

## 持久化工单管理示例

阶段二在意图识别之上增加由 Java 控制的工单创建、列表和详情，并通过
`scaffold-orm-starter`、Spring Data JPA 和 PostgreSQL 持久化：

```http
POST /api/examples/support/work-orders
Content-Type: application/json

{
  "requestId": "request_0001",
  "description": "手机无法开机，订单号202607190001，我要申请退款"
}
```

```http
GET /api/examples/support/work-orders
GET /api/examples/support/work-orders/{id}
```

应用从 Spring Security 上下文读取已登录用户 ID，请求和模型都不能传入 `userId`。工单 ID、conversationId、状态和
创建时间均由 Java 生成；相同用户使用相同 `requestId` 重复提交时返回同一工单。默认连接
`jdbc:postgresql://localhost:5432/scaffold_ai`，可通过 `SCAFFOLD_DB_URL`、`SCAFFOLD_DB_USERNAME`
和 `SCAFFOLD_DB_PASSWORD` 覆盖。PostgreSQL 是工单、订单、完整聊天历史和知识文档的唯一事实源；
H2 仅用于自动化测试。

本地可运行 `docker compose up -d` 启动带持久化卷的 PostgreSQL/pgvector 和 Redis。Redis 只用于缓存等
可重建数据，不保存唯一业务数据。进入生产前应使用 Flyway 或 Liquibase 管理 Schema。

## 订单和物流 Tool Calling

阶段三提供只读的 `query_order`、`query_logistics` 和 `query_product` 工具。模型只负责选择工具和
提供用户明确输入的订单号；当前用户和 requestId 由服务端通过 `ToolContext` 注入，不能由模型
或客户端覆盖。演示订单会幂等初始化到 PostgreSQL，其中首次初始化的 `admin` 用户（ID `1`）可以访问订单
`202607190001`。

```http
POST /api/examples/support/assistant/chat
Content-Type: application/json

{
  "workOrderId": 42,
  "message": "订单202607190001的物流到哪里了"
}
```

Tool 必须经过 `OrderService` 和 `SupportAuthorizationService` 才能访问 JPA Repository，返回给模型的
DTO 不包含用户 ID、收件手机号等敏感字段。订单 `202607190002` 属于其他演示用户，可用于
验证越权查询会被拒绝。

聊天请求不接受客户端提供的 `conversationId`，只接受当前用户可访问的 `workOrderId`。服务端使用创建
工单时生成的会话标识恢复多轮上下文。用户和助手消息同时持久化到 PostgreSQL，前端刷新或切换工单时通过
`GET /api/examples/support/work-orders/{id}/messages` 恢复最近 20 条记录。关闭工单并清理会话记忆：

```http
POST /api/examples/support/work-orders/42/close
```

## 售后知识库 RAG

阶段五第一版从 `src/main/resources/knowledge` 加载退款、保修、物流和手机排障资料。只有检索到相关资料
才调用模型；没有依据时直接拒答，避免模型凭常识猜测。

```http
POST /api/examples/support/knowledge/answer
Content-Type: application/json

{
  "workOrderId": 42,
  "question": "手机无法开机应该怎样处理？"
}
```

响应 `KnowledgeAnswer` 包含 `grounded`、回答正文以及带 `documentId`、版本和更新时间的来源列表。
知识文档会幂等写入 PostgreSQL，并使用本地量化 `bge-small-zh-v1.5` ONNX 模型生成 512 维 Embedding。
应用启动时将文档切分并重建 `ai_knowledge_vector`，查询通过 pgvector 执行 Top-K 相似度检索和最低相关度
过滤；重建会先清除旧向量，因此文档更新和删除不会留下过期片段。模型文件缓存在
`${user.home}/.cache/scaffold-ai/onnx/bge-small-zh-v1.5`。

## 综合处理建议

阶段六将当前用户可访问的工单、只读订单摘要和 pgvector 检索到的知识片段汇总为结构化建议。建议及其
推荐步骤、风险等级、知识来源和事实证据均持久化到 PostgreSQL；服务只读取业务事实，不会修改工单、
订单或退款状态。高风险工单以及同时缺少订单和知识依据的场景由 Java 规则强制转人工审核。

```http
POST /api/examples/support/work-orders/{workOrderId}/suggestions
GET /api/examples/support/work-orders/{workOrderId}/suggestions/latest
```

前端选择工单后可生成或恢复最近一次建议，并同时查看诊断、推荐操作、证据来源与风险提示。

## 退款准备与二次确认

阶段七只向模型暴露 `prepare_refund`：它根据当前用户可访问的订单生成十分钟有效的退款确认卡片，
不会修改订单。确认标识使用安全随机数，并与用户、订单号、金额和退款原因一起持久化到 PostgreSQL。
最终退款只能由前端在用户明确点击后调用普通业务接口：

```http
POST /api/examples/support/refunds/prepare
POST /api/examples/support/refunds/confirm
POST /api/examples/support/refunds/cancel
```

确认时服务端使用数据库锁重新检查订单归属、金额、订单状态和售后状态；重复确认返回首次执行结果，
不会重复退款。准备、确认、执行、取消和过期事件全部写入 `ai_refund_audit`。
