package com.scaffold.support.conversation;

import com.scaffold.support.identity.SupportCurrentUserProvider;
import com.scaffold.support.workorder.OrderNotAccessibleException;
import com.scaffold.support.workorder.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * 统一管理工单消息的持久化顺序和 AI 会话记忆，避免内存状态成为事实来源。
 */
@Service
@RequiredArgsConstructor
public class SupportConversationService {

    private static final int NOT_DELETED = 0;
    private final WorkOrderRepository workOrders;
    private final SupportMessageRepository messages;
    private final SupportCurrentUserProvider currentUser;

    /**
     * 查询当前用户可访问工单的最近消息。
     */
    @Transactional(readOnly = true)
    public List<SupportMessageRes> history(long workOrderId) {
        requireOwnedWorkOrder(workOrderId);
        List<SupportMessageEntity> latest = new ArrayList<>(
                messages.findTop20ByWorkOrderIdOrderBySequenceDesc(workOrderId));
        latest.sort(java.util.Comparator.comparingLong(SupportMessageEntity::getSequence));
        return latest.stream().map(this::toDomain).toList();
    }

    /**
     * 按工单内递增序号追加一条持久化消息。
     */
    @Transactional
    public void append(long workOrderId, String role, String content) {
        SupportMessageEntity entity = new SupportMessageEntity();
        entity.setWorkOrderId(workOrderId);
        entity.setSequence(messages.findMaxSequence(workOrderId) + 1);
        entity.setRole(role);
        entity.setContent(content);
        messages.save(entity);
    }

    /**
     * 删除指定工单的持久化消息，通常在关闭工单时调用。
     */
    @Transactional
    public void clear(long workOrderId) {
        messages.deleteByWorkOrderId(workOrderId);
    }

    @Transactional(readOnly = true)
    /** 从 PostgreSQL 消息记录恢复 Spring AI ChatMemory。 */
    public void restoreMemory(long workOrderId, String conversationId, ChatMemory memory) {
        if (!memory.get(conversationId).isEmpty()) {
            return;
        }
        List<SupportMessageEntity> latest = new ArrayList<>(
                messages.findTop20ByWorkOrderIdOrderBySequenceDesc(workOrderId));
        latest.sort(java.util.Comparator.comparingLong(SupportMessageEntity::getSequence));
        List<Message> restored = latest.stream().map(message -> "USER".equals(message.getRole())
                ? new UserMessage(message.getContent())
                : new AssistantMessage(message.getContent())).map(Message.class::cast).toList();
        if (!restored.isEmpty()) {
            memory.add(conversationId, restored);
        }
    }

    private void requireOwnedWorkOrder(long workOrderId) {
        if (workOrderId <= 0 || workOrders.findByIdAndUserIdAndDeleted(
                workOrderId, currentUser.requireUserId(), NOT_DELETED).isEmpty()) {
            throw new OrderNotAccessibleException();
        }
    }

    private SupportMessageRes toDomain(SupportMessageEntity entity) {
        return new SupportMessageRes(entity.getId(), entity.getSequence(), entity.getRole(), entity.getContent(),
                entity.getGmtCreated().toInstant(ZoneOffset.UTC));
    }
}
