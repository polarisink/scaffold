# scaffold-ai-starter

`scaffold-ai-starter` 是面向业务应用的 Spring AI Starter，从 MateCloud `mate-ai-starter` 提取并适配
Spring Boot 3.5 / Spring AI 1.1。它提供同步聊天、SSE 流式聊天、按会话 ID 保存的窗口记忆、Advisor
链、`@Tool` 自动发现、工具清单与直接调用 API。

本文档除介绍 Starter 的接入方式外，也给出一条适合 Java 业务开发者的 AI 应用开发路线。目标是使用
大模型解决业务问题，而不是训练大模型或转型为算法工程师。

## 定位与边界

Java 开发者可以把大模型看成一种“能够理解自然语言，但输出具有不确定性的外部服务”。AI 应用开发
仍然以业务建模、接口集成、数据库、权限、安全、测试和可观测性为核心。

如果目标是在现有业务中使用 AI，初期不必深入学习：

- Transformer 的数学细节
- PyTorch、CUDA 和分布式训练
- 从头训练、部署大模型
- 复杂微调和多智能体自治框架
- 仅仅为了开发 AI 应用而转向 Python

只需先理解 Token、上下文窗口、Embedding 和模型幻觉等基本概念，然后围绕结构化输出、Tool
Calling、RAG 与工程治理逐步实践。

## 核心能力

### 1. 模型调用与结构化输出

不要只停留在聊天接口。业务系统更常见的需求是让模型把非结构化文本转换为稳定的 Java 对象：

```java
public record WorkOrderIntent(
        String category,
        String summary,
        int priority
) {}
```

典型场景包括工单分类、文本摘要、信息提取、内容审核、意图识别和报告生成。此类功能边界清晰、
容易准备测试样本，适合作为第一个落地项目。

### 2. Prompt 工程

Prompt 应当像代码和配置一样被维护，而不是散落在 Java 字符串中：

```text
角色：你是客服工单分类助手。

任务：从用户输入中提取类别、摘要和优先级。

约束：
1. 不确定时将类别标记为 unknown。
2. 不得虚构用户未提供的信息。
3. 仅返回约定的数据结构。

输入：
{{userInput}}
```

建议为 Prompt 设置版本号，保存固定测试样例，并在更换 Prompt 或模型后执行回归测试。

Starter 提供 `AiPromptTemplate`，用于从 classpath 加载并渲染一组带名称和版本的 system/user
模板。通用模板基础设施放在 Starter，具体业务 Prompt 应放在最终应用或示例模块：

```java
AiPromptTemplate template = AiPromptTemplate.from(
        new AiPromptMetadata("support-intent", "v1"),
        systemResource,
        userResource);

RenderedAiPrompt prompt = template.render(Map.of("message", userMessage));
WorkOrderIntent result = aiChatService.entity(conversationId, prompt, WorkOrderIntent.class);
```

完整示例见 `scaffold-test/scaffold-test-ai`。生产接口不应允许客户端覆盖 system prompt；服务端应根据
业务用例选择已审核的 Prompt 名称和版本。

### 3. Tool Calling

Tool Calling 让模型负责理解用户意图和选择工具，让 Java 方法负责真实业务执行：

```java
@Component
public class OrderTools {

    private final OrderService orderService;

    public OrderTools(OrderService orderService) {
        this.orderService = orderService;
    }

    @Tool(description = "根据订单号查询当前订单和物流状态")
    public OrderInfo queryOrder(String orderId) {
        return orderService.query(orderId);
    }
}
```

本 Starter 会自动发现 Spring Bean 中的 `@Tool` 方法，并将其注册给 ChatClient。设计工具时应坚持：

- 模型不能绕过业务服务和权限体系直接访问数据库
- Java 代码必须再次执行身份认证、权限检查与参数校验
- 查询类工具可以适度自动化
- 退款、删除、转账、发送消息等有副作用的操作必须二次确认
- 工具应保持小而明确，描述清楚输入、输出和使用条件
- 业务系统掌握最终控制权，模型只负责理解、建议或编排

### 4. RAG 企业知识检索

RAG（检索增强生成）用于让模型基于企业文档回答问题：

```text
业务文档
  -> 文本提取与切分
  -> 生成 Embedding
  -> 保存到向量数据库
  -> 根据问题检索相关片段
  -> 将片段和问题交给模型生成答案
```

它适合企业知识库、产品说明书问答、售后政策查询、合同辅助分析和代码规范助手。初期可以使用
PostgreSQL + pgvector：原始文件存对象存储，文档元数据、版本和权限仍由关系表管理。

第一版应优先保证：

- 回答展示引用来源
- 没有可靠依据时明确回答“不知道”
- 检索时执行用户、组织或租户的数据权限过滤
- 文档更新后可以追踪、重建或删除对应向量

不要在第一版就引入知识图谱、GraphRAG 或复杂的多路召回。

### 5. 测试与可观测性

大模型输出不总能使用普通的 `assertEquals` 验证。应维护一组来自真实业务的固定案例，至少检查：

- 分类、提取和回答结果是否满足关键规则
- 是否遗漏关键信息或产生没有依据的内容
- 是否选择了正确的工具及参数
- 是否发生越权或执行了未确认的副作用
- 响应时间、失败率、Token 用量和调用成本
- Prompt、模型或知识库变化后是否出现效果退化

生产环境需要记录模型、Prompt 版本、会话 ID、工具调用、耗时、Token 和错误信息。日志中不得泄露
密钥、完整个人信息或其他敏感数据。

## 推荐学习顺序

### 第一阶段：模型 API（3～5 天）

实现普通问答、流式响应和结构化输出，并处理超时、重试、限流与 Token 统计。

### 第二阶段：业务工具调用（约 1 周）

实现一个订单客服助手：查询订单、物流和退款规则，生成处理建议，涉及退款时进入人工确认流程。
相比通用聊天机器人，这类项目更容易验证业务价值。

### 第三阶段：RAG（1～2 周）

导入产品文档和售后规则，实现文档切分、向量检索、答案引用、无依据拒答和文档权限过滤。

### 第四阶段：生产化（1～2 周）

补齐 Prompt 版本管理、脱敏、链路追踪、评测数据集、成本告警、降级策略和人工审核节点。

### 第五阶段：按需学习 MCP 与 Agent

MCP 可以理解为 AI 应用连接外部工具和数据的标准接口。只有当工具需要在多个 AI 客户端之间复用
时，才有必要建设 MCP Server。推荐顺序如下：

```text
模型调用 -> 结构化输出 -> Tool Calling -> RAG -> MCP -> 按需使用 Agent
```

不要把“自主 Agent”作为业务 AI 的默认起点。步骤固定、风险较高的流程应优先使用普通 Java
工作流，由模型只处理其中适合自然语言理解的节点。

## 系统职责划分

模型不应替代数据库、权限系统、规则引擎、精确计算器或业务事实来源。推荐的职责划分是：

```text
用户自然语言
    -> 大模型理解意图、提取参数
    -> Java 执行权限检查、参数校验和业务操作
    -> 大模型将结果组织为易读内容
```

对于金额计算、库存扣减、状态流转、合规判断等确定性逻辑，应由 Java 代码完成。模型输出默认视为
不可信输入，必须经过校验后才能进入核心业务链路。

## Starter 接入

### 添加依赖

```xml
<dependency>
    <groupId>com.scaffold</groupId>
    <artifactId>scaffold-ai-starter</artifactId>
</dependency>
```

Starter 保持模型提供商中立。业务应用按需增加 `spring-ai-starter-model-openai`、
`spring-ai-starter-model-ollama` 等模型依赖；测试模块可提供无需联网的本地 `ChatModel`。

### 基础配置

配置前缀为 `scaffold.ai`，模型提供商继续使用 Spring AI 原生的 `spring.ai.*` 配置：

```yaml
scaffold:
  ai:
    enabled: true
    system-prompt: 你是业务系统助手。仅根据可用数据回答，并在需要时调用工具。
    memory-max-messages: 20
    default-conversation-id: default
    advisor-logging-enabled: true
    safe-guard-words: []
    security:
      enabled: true
      header: X-AI-API-Key
      api-key: ${AI_API_KEY}
```

生产环境应启用 `scaffold.ai.security.enabled`，并通过环境变量或密钥管理服务提供 API Key，不要将
真实密钥提交到仓库。

### HTTP 接口

- `POST /api/ai/chat`：同步聊天
- `POST /api/ai/chat/stream`：SSE 流式聊天
- `GET /api/ai/tools`：查询已注册工具
- `POST /api/ai/tools/{name}/invoke`：直接调用指定工具

同步和流式聊天请求示例：

```json
{
  "conversationId": "user-1001",
  "system": null,
  "message": "查询订单 20260718001 的物流状态"
}
```

`conversationId` 不应直接使用可伪造的前端值承载权限。正式应用应从已认证用户或租户上下文生成
会话标识，并限制会话数据的访问范围。

## 生产检查清单

- 明确业务目标和成功指标，而不是只做一个聊天页面
- Prompt、模型参数和知识库版本可追踪、可回滚
- 所有 Tool 在 Java 侧执行认证、授权、校验和审计
- 有副作用的 Tool 默认要求用户确认，并支持幂等和防重放
- RAG 在检索阶段执行租户及数据权限过滤
- 维护覆盖正常、边界、恶意输入和越权场景的评测集
- 设置模型超时、重试、并发限制、成本预算和降级响应
- 对用户输入、检索内容和模型输出执行必要的敏感信息处理
- 监控响应时间、错误率、Token、成本、工具成功率和拒答率

## 进一步阅读

- [Spring AI API](https://docs.spring.io/spring-ai/reference/api/)
- [Spring AI Tool Calling](https://docs.spring.io/spring-ai/reference/api/tools.html)
- [Spring AI Observability](https://docs.spring.io/spring-ai/reference/observability/)
- [Model Context Protocol 架构](https://modelcontextprotocol.io/docs/learn/architecture)

一句话原则：继续发挥 Java 工程能力，重点掌握模型 API、结构化输出、Tool Calling、RAG 和 AI
测试，不要把主要时间耗在训练模型与追逐复杂 Agent 框架上。
