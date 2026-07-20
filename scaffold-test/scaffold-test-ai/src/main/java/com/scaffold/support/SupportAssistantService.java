package com.scaffold.support;

import com.scaffold.ai.chat.AiChatService;
import com.scaffold.support.order.OrderNotAccessibleException;
import com.scaffold.support.repository.WorkOrderEntity;
import com.scaffold.support.repository.WorkOrderRepository;
import com.scaffold.support.model.WorkOrderStatus;
import com.scaffold.support.security.SupportCurrentUserProvider;
import com.scaffold.support.tool.SupportToolContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupportAssistantService {

    private static final int MAX_MESSAGE_LENGTH = 4_000;
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

    public String chat(long workOrderId, String message) {
        String validatedMessage = validateMessage(message);
        long userId = currentUserProvider.requireUserId();
        WorkOrderEntity workOrder = workOrderRepository.findByIdAndUserIdAndDeleted(workOrderId, userId, 0)
                .orElseThrow(OrderNotAccessibleException::new);
        if (workOrder.getStatus() == WorkOrderStatus.CLOSED) {
            throw new IllegalStateException("work order conversation is closed");
        }
        Map<String, Object> context = Map.of(
                SupportToolContext.USER_ID, userId,
                SupportToolContext.REQUEST_ID, UUID.randomUUID().toString());
        return aiChatService.chat(workOrder.getConversationId(), SYSTEM_PROMPT, validatedMessage, context, READ_ONLY_TOOLS);
    }

    private String validateMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
        String normalized = message.trim();
        if (normalized.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("message must not exceed " + MAX_MESSAGE_LENGTH + " characters");
        }
        return normalized;
    }
}
