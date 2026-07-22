package com.scaffold.support.workorder;

import com.scaffold.support.conversation.SupportConversationService;
import com.scaffold.support.identity.SupportCurrentUserProvider;
import com.scaffold.support.intent.AnalyzeRequest;
import com.scaffold.support.intent.SupportIntentService;
import com.scaffold.support.intent.WorkOrderIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

/**
 * 售后工单应用服务，集中处理创建幂等性、用户数据隔离和关闭状态流转。
 */
@Service
@RequiredArgsConstructor
public class WorkOrderService {

    private static final int NOT_DELETED = 0;

    private final SupportIntentService intentService;
    private final WorkOrderRepository repository;
    private final SupportCurrentUserProvider currentUserProvider;
    private final ChatMemory chatMemory;
    private final SupportConversationService conversationService;

    /**
     * 根据客户端请求标识幂等创建工单；重复请求直接返回已有工单，不重复调用模型。
     */
    @Transactional
    public WorkOrder create(CreateWorkOrderRequest request) {
        long userId = currentUserProvider.requireUserId();
        String requestId = request.requestId().trim();
        String description = request.description().trim();
        return repository.findByUserIdAndRequestIdAndDeleted(userId, requestId, NOT_DELETED)
                .map(this::toDomain)
                .orElseGet(() -> createNew(userId, requestId, description));
    }

    /**
     * 查询当前登录用户的全部未删除工单。
     */
    @Transactional(readOnly = true)
    public List<WorkOrder> listCurrentUserWorkOrders() {
        return repository.findByUserIdAndDeletedOrderByGmtCreatedDescIdDesc(
                        currentUserProvider.requireUserId(), NOT_DELETED).stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * 查询当前用户可访问的单个工单。
     */
    @Transactional(readOnly = true)
    public WorkOrder getCurrentUserWorkOrder(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("work order id must be positive");
        }
        long userId = currentUserProvider.requireUserId();
        return repository.findByIdAndUserIdAndDeleted(id, userId, NOT_DELETED)
                .map(this::toDomain)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "工单不存在"));
    }

    /**
     * 关闭当前用户工单，并同步清理内存记忆和持久化对话。
     */
    @Transactional
    public WorkOrder close(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("work order id must be positive");
        }
        long userId = currentUserProvider.requireUserId();
        WorkOrderEntity entity = repository.findByIdAndUserIdAndDeleted(id, userId, NOT_DELETED)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "工单不存在"));
        entity.setStatus(WorkOrderStatus.CLOSED);
        WorkOrder closed = toDomain(repository.saveAndFlush(entity));
        chatMemory.clear(entity.getConversationId());
        conversationService.clear(id);
        return closed;
    }

    private WorkOrder createNew(long userId, String requestId, String description) {
        String conversationId = "work-order:" + UUID.randomUUID();
        WorkOrderIntent intent = intentService.analyze(new AnalyzeRequest(conversationId, description));
        WorkOrderStatus status = intent.manualReviewRequired()
                ? WorkOrderStatus.MANUAL_REVIEW : WorkOrderStatus.OPEN;
        WorkOrderEntity entity = new WorkOrderEntity();
        entity.setUserId(userId);
        entity.setRequestId(requestId);
        entity.setConversationId(conversationId);
        entity.setCategory(intent.category());
        entity.setSummary(intent.summary());
        entity.setPriority(intent.priority());
        entity.setStatus(status);
        entity.setOrderNo(intent.orderNo());
        entity.setManualReviewRequired(intent.manualReviewRequired());
        entity.setOriginalDescription(description);
        return toDomain(repository.saveAndFlush(entity));
    }

    private WorkOrder toDomain(WorkOrderEntity entity) {
        return new WorkOrder(entity.getId(), entity.getUserId(), entity.getRequestId(),
                entity.getConversationId(), entity.getCategory(), entity.getSummary(), entity.getPriority(),
                entity.getStatus(), entity.getOrderNo(), entity.getManualReviewRequired(),
                entity.getOriginalDescription(), entity.getGmtCreated().toInstant(ZoneOffset.UTC));
    }

}
