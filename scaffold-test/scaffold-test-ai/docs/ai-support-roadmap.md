# AI 智能售后工单系统建设路线

## 目标

`scaffold-test-ai` 不堆积多个互不相关的 AI Demo，而是逐步建设一个 **AI 智能售后工单系统
（Scaffold AI Support）**。用户以自然语言描述售后问题，系统理解问题、查询业务数据、检索规则、
生成处理建议，并在用户确认后执行受控操作。

目标流程：

```text
用户描述售后问题
    -> AI 提取工单意图和业务参数
    -> Java 校验并创建工单
    -> AI 按需查询订单、物流和商品
    -> RAG 检索售后规则与产品说明
    -> AI 生成带依据的处理建议
    -> 查询类操作自动完成
    -> 退款等写操作生成待确认申请
    -> 用户确认或人工审核
    -> Java 执行业务操作并记录审计
```

这个系统用于覆盖 `scaffold-ai-starter` 的主要能力：

| Starter/AI 能力 | 售后业务中的用途 |
| --- | --- |
| 同步和流式聊天 | 用户补充问题、实时生成处理建议 |
| Prompt 模板 | 意图提取、知识问答、建议生成 |
| 结构化输出 | 自然语言转换为工单和建议对象 |
| 会话记忆 | 保存同一工单的多轮沟通 |
| Tool Calling | 查询订单、物流、商品 |
| ToolContext | 传递可信用户、租户和请求信息 |
| 权限与参数校验 | 用户只能操作有权访问的数据 |
| RAG | 检索售后制度和产品手册 |
| 二次确认 | 控制退款、取消订单等副作用操作 |
| 可观测性 | 记录耗时、Token、工具调用和错误 |
| AI 评测 | 检查分类、工具选择、引用和安全性 |
| MCP | 后期复用稳定的订单、物流工具 |

## 建设原则

- 模型负责理解自然语言、提取参数、选择工具和生成建议。
- Java 负责身份认证、权限、参数校验、金额计算、状态流转和最终执行。
- 模型输入、输出和工具参数默认视为不可信数据。
- Tool 只能调用业务 Service，不直接访问 Mapper 或 Repository。
- 查询操作可以在授权后自动执行；有副作用的操作必须二次确认。
- 每个阶段都应当可运行、可测试，不为后续阶段提前建设复杂平台。
- Prompt 使用文件和显式版本管理，升级时新增版本而不是覆盖旧版本。

## 规划目录

随着功能逐步增加，业务代码统一收敛到 `com.scaffold.ai.support`：

```text
src/main/java/com/scaffold/ai/support/
├── controller/
│   ├── SupportController.java
│   ├── SupportKnowledgeController.java
│   └── SupportConfirmationController.java
├── service/
│   ├── SupportAssistantService.java
│   ├── WorkOrderService.java
│   ├── OrderService.java
│   ├── KnowledgeService.java
│   ├── RefundService.java
│   └── AiEvaluationService.java
├── tool/
│   ├── OrderQueryTools.java
│   ├── LogisticsQueryTools.java
│   └── RefundPreparationTools.java
├── model/
│   ├── WorkOrder.java
│   ├── WorkOrderIntent.java
│   ├── OrderSummary.java
│   ├── KnowledgeAnswer.java
│   ├── HandlingSuggestion.java
│   └── PendingAction.java
├── repository/
│   ├── WorkOrderRepository.java
│   ├── DemoOrderRepository.java
│   └── PendingActionRepository.java
├── security/
│   ├── AiActor.java
│   ├── AiActorProvider.java
│   └── SupportAuthorizationService.java
└── evaluation/
    ├── EvaluationCase.java
    └── EvaluationService.java
```

Prompt 统一放在：

```text
src/main/resources/prompts/support/
├── intent/v1/
│   ├── system.st
│   └── user.st
├── answer/v1/
│   ├── system.st
│   └── user.st
├── suggestion/v1/
│   ├── system.st
│   └── user.st
└── summary/v1/
    ├── system.st
    └── user.st
```

目录是演进目标，不要求第一阶段一次性全部创建。

## 当前状态

- [x] Starter 支持同步和流式聊天、会话记忆、Tool Calling。
- [x] Starter 支持从 classpath 加载并渲染版本化 Prompt。
- [x] 已有 `WorkOrderIntent` 结构化输出示例。
- [x] 已有 `support/intent/v1` system/user Prompt。
- [x] 已有不调用真实模型的 Prompt 渲染与业务调用单元测试。
- [x] 已将 `customer` 示例演进为统一的 `support` 业务包。
- [x] 已增加阶段一工单意图分析界面。
- [x] 已使用 `scaffold-orm-starter` 完成阶段二的数据库持久化、幂等创建和当前用户隔离。

## 阶段一：从描述中提取工单意图

阶段一将原有客服意图示例演进为：

```java
public record WorkOrderIntent(
        WorkOrderCategory category,
        String summary,
        int priority,
        String orderNo,
        boolean manualReviewRequired
) {}
```

示例输入：

```text
我买的手机无法开机，订单号是202607190001，我想退款。
```

示例输出：

```json
{
  "category": "REFUND",
  "summary": "用户购买的手机无法开机并申请退款",
  "priority": 4,
  "orderNo": "202607190001",
  "manualReviewRequired": true
}
```

学习内容：Prompt 文件化、Prompt 版本、结构化输出、枚举和字段校验、模型异常处理。

完成标准：

- [x] `customer` 包迁移为 `support` 包。
- [x] Prompt 迁移到 `prompts/support/intent/v1`。
- [x] 服务端拒绝空消息、超长消息和不合法的结构化结果。
- [x] 提供意图分析表单并展示结构化结果。
- [x] 测试正常输入、缺失订单号和 Prompt 注入输入。

## 阶段二：创建和管理售后工单

增加普通 Java 领域对象和 Service：

```java
public record WorkOrder(
        Long id,
        Long userId,
        String conversationId,
        WorkOrderCategory category,
        String summary,
        int priority,
        WorkOrderStatus status,
        String orderNo,
        Instant createdAt
) {}
```

流程为“AI 提取 `WorkOrderIntent` -> Java 校验 -> `WorkOrderService` 创建工单”。AI 不负责生成
数据库 ID、用户 ID、创建时间或控制状态流转。

当前使用 `scaffold-orm-starter`、Spring Data JPA 和文件型 H2。`WorkOrderEntity` 继承
`BaseAuditable`，数据库通过 `(user_id, request_id)` 唯一约束保护幂等语义。演示环境允许 Hibernate
自动更新表结构；生产环境应改用 MySQL 或 PostgreSQL，并用数据库迁移工具管理 Schema。

完成标准：

- [x] 创建、查询和列出工单。
- [x] 工单绑定当前用户，而不是使用模型生成的 userId。
- [x] 工单状态由 Java 枚举和 Service 控制。
- [x] 同一个提交请求具备基本幂等能力。
- [x] 前端可以查看工单列表和详情。
- [x] 应用重启后工单数据仍然保留。

## 阶段三：订单和物流 Tool Calling

增加只读工具：

```text
query_order
query_logistics
query_product
```

Tool 方法只负责接收模型参数、读取可信 `ToolContext`、校验参数并调用业务 Service：

```text
模型参数
    -> Tool 参数校验
    -> ToolContext 当前用户/租户
    -> OrderService
    -> SupportAuthorizationService
    -> DemoOrderRepository
    -> 安全 DTO
```

第一版使用内存订单数据，不连接真实订单系统。

完成标准：

- [x] `ToolContext` 中包含服务端提供的用户、租户和 requestId。
- [x] 模型不能传入或覆盖 userId、tenantId。
- [x] Tool 不直接访问 Repository。
- [x] 用户只能查询自己的订单。
- [x] 返回 `OrderSummary`，不向模型暴露完整实体和敏感字段。
- [x] 每次对话只暴露售后场景真正需要的工具。
- [x] 测试合法查询、越权查询、错误订单号和工具参数幻觉。

## 阶段四：工单多轮对话

示例：

```text
用户：手机坏了。
AI：请提供订单号。
用户：202607190001。
AI：已查询到订单，请描述具体故障。
用户：完全无法开机。
```

使用 Chat Memory 保存同一工单的对话窗口，并将会话与用户、工单绑定。不要把前端任意传入的
`conversationId` 当作权限依据，可使用服务端生成的 `userId:workOrderId` 或随机不可猜测标识。

完成标准：

- [x] 创建工单时由服务端生成 conversationId。
- [x] 不同用户和工单之间的记忆完全隔离。
- [x] 限制历史消息数量和单条消息长度。
- [x] 工单关闭后可以清理或归档会话。
- [x] 测试跨用户 conversationId 访问。

## 阶段五：售后知识库 RAG

准备少量可验证的演示文档：

```text
knowledge/
├── refund-policy.md
├── warranty-policy.md
├── logistics-policy.md
└── phone-troubleshooting.md
```

先使用内存 VectorStore 跑通文档加载、切分、Embedding、检索和回答，再迁移到 PostgreSQL +
pgvector。回答必须附带来源，没有可靠片段时明确拒答。

完成标准：

- [ ] 文档具有 documentId、版本和更新时间。
- [ ] 检索结果执行用户或租户权限过滤。
- [ ] `KnowledgeAnswer` 包含答案和引用来源。
- [ ] 没有相关资料时不允许模型凭常识猜测。
- [ ] 文档更新和删除后可以同步处理向量。
- [ ] 测试正确引用、无依据拒答和跨租户检索。

## 阶段六：生成综合处理建议

综合用户描述、工单数据、订单工具结果和知识库内容，生成：

```java
public record HandlingSuggestion(
        String diagnosis,
        List<String> recommendedActions,
        List<KnowledgeSource> sources,
        RiskLevel riskLevel,
        boolean manualReviewRequired
) {}
```

Java 负责调用顺序和必要的状态机，模型负责理解、工具选择和自然语言建议，不引入不必要的多智能体。

完成标准：

- [ ] 建议结果为受校验的结构化对象。
- [ ] 事实性结论可以追溯到订单数据或知识来源。
- [ ] 建议不会直接改变工单、订单或退款状态。
- [ ] 高风险和信息不足的建议自动标记人工审核。
- [ ] 前端同时展示建议、依据和风险提示。

## 阶段七：退款准备与二次确认

模型只允许调用 `prepare_refund` 创建 `PendingAction`，不能直接执行退款：

```java
public record PendingAction(
        String confirmationId,
        Long userId,
        ActionType action,
        String summary,
        Instant expiresAt
) {}
```

前端展示订单、金额、原因和有效期，用户点击确认按钮后调用普通业务接口。不要把 `confirm_refund`
同时暴露给当前模型，否则模型可能在同一轮连续准备并确认。

完成标准：

- [ ] confirmationId 与当前用户、租户和具体操作参数绑定。
- [ ] confirmationId 短期有效、只能使用一次且不可预测。
- [ ] 确认时重新检查权限、订单状态、金额和业务规则。
- [ ] 最终执行具备幂等能力。
- [ ] 取消、过期、重复确认和越权确认都有测试。
- [ ] 所有准备、确认和执行动作进入审计日志。

## 阶段八：生产化与 AI 评测

建立固定评测数据集，覆盖：

- 意图和优先级识别
- 必填参数提取
- 工具选择及参数
- RAG 引用和无依据拒答
- Prompt 注入
- 数据越权
- 未确认的副作用操作

每次评测记录 Prompt 版本、模型、是否通过、耗时、Token 和错误原因。

完成标准：

- [ ] Prompt 和模型升级前能够运行回归评测。
- [ ] 记录请求耗时、错误率、Token 和工具成功率。
- [ ] 日志不包含 API Key、完整个人信息或其他敏感数据。
- [ ] 设置模型超时、重试、并发和成本限制。
- [ ] 模型不可用时有明确降级响应和人工处理入口。

## 阶段九：按需增加 MCP

只有当订单、物流等工具已经稳定，并需要被其他 AI 客户端复用时，才将其暴露为 MCP Server。
MCP 不是当前阶段的前置条件。

完成标准：

- [ ] 明确哪些工具允许跨应用暴露。
- [ ] MCP 调用复用已有业务 Service、权限与审计，不另写旁路逻辑。
- [ ] 验证认证、授权、Prompt 注入和工具滥用风险。

## 前端演进

前端始终保持一个售后工作台，而不是为每个 AI 能力创建孤立页面：

```text
┌──────────────────────────────────────────────┐
│ Scaffold AI Support                          │
├───────────────┬──────────────────────────────┤
│ 工单列表      │ 工单详情                     │
│               │ 用户问题和多轮对话           │
│ 待处理        │ 结构化意图                   │
│ 处理中        │ 订单和物流摘要               │
│ 待确认        │ 知识引用                     │
│ 已完成        │ AI 处理建议                  │
│               │ 退款确认卡片                 │
└───────────────┴──────────────────────────────┘
```

每完成一个后端阶段，只在同一工作台增加相应展示和操作能力。

## 实施顺序

严格按以下顺序推进，每个阶段完成并测试后再进入下一阶段：

```text
结构化工单意图
    -> Java 工单管理
    -> 只读 Tool Calling
    -> 身份与数据权限
    -> 多轮会话
    -> RAG 知识库
    -> 综合处理建议
    -> 副作用操作二次确认
    -> 可观测性与 AI 评测
    -> 按需使用 MCP
```

阶段一、二已经完成。下一步是阶段三：增加订单和物流的只读 Tool Calling，并通过可信
`ToolContext` 传递用户身份。现阶段继续使用内存数据，不提前实现数据库、向量库或退款执行。
