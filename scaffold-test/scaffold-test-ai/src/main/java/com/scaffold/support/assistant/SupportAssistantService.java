package com.scaffold.support.assistant;

import com.scaffold.ai.chat.AiChatService;
import com.scaffold.support.conversation.SupportConversationService;
import com.scaffold.support.identity.SupportCurrentUserProvider;
import com.scaffold.support.order.SupportToolContext;
import com.scaffold.support.workorder.OrderNotAccessibleException;
import com.scaffold.support.workorder.WorkOrderEntity;
import com.scaffold.support.workorder.WorkOrderRepository;
import com.scaffold.support.workorder.WorkOrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.memory.ChatMemory;

import java.util.Map;
import java.util.UUID;

/**
 * 在指定工单会话中调用 AI 助手，并保证工具上下文中的用户身份由服务端提供。
 */
@Service
@RequiredArgsConstructor
public class SupportAssistantService {

    static final String[] READ_ONLY_TOOLS = {"query_order", "query_logistics", "query_product"};

    private static final String SYSTEM_PROMPT = """
            你是 Scaffold 售后订单助手。
            只能根据工具返回的数据陈述订单、物流和商品事实。
            用户没有明确提供订单号时，应请用户提供，不得猜测、补全或生成订单号。
            工具未找到当前用户可访问的订单时，不得推断订单是否真实存在。
            你只能提供查询结果和售后建议，不得执行或声称已经执行退款、取消订单、修改地址等操作。
            """;

    private final AiChatService aiChatService;
    private final SupportCurrentUserProvider currentUserProvider;
    private final WorkOrderRepository workOrderRepository;
    private final SupportConversationService conversationService;
    private final ChatMemory chatMemory;

    /**
     * 发送一轮工单消息。调用模型前恢复历史记忆，调用完成后持久化用户和助手消息。
     */
    public String chat(ChatRequest request) {
        long workOrderId = request.workOrderId();
        String message = request.message().trim();
        long userId = currentUserProvider.requireUserId();
        WorkOrderEntity workOrder = workOrderRepository.findByIdAndUserIdAndDeleted(workOrderId, userId, 0)
                .orElseThrow(OrderNotAccessibleException::new);
        if (workOrder.getStatus() == WorkOrderStatus.CLOSED) {
            throw new IllegalStateException("work order conversation is closed");
        }
        Map<String, Object> context = Map.of(
                SupportToolContext.USER_ID, userId,
                SupportToolContext.REQUEST_ID, UUID.randomUUID().toString());
        conversationService.restoreMemory(workOrderId, workOrder.getConversationId(), chatMemory);
        conversationService.append(workOrderId, "USER", message);
        String answer = aiChatService.chat(workOrder.getConversationId(), SYSTEM_PROMPT, message, context, READ_ONLY_TOOLS);
        conversationService.append(workOrderId, "ASSISTANT", answer);
        return answer;
    }

}
