package com.scaffold.support;

import com.scaffold.support.model.WorkOrder;
import com.scaffold.support.model.WorkOrderIntent;
import com.scaffold.support.model.WorkOrderStatus;
import com.scaffold.support.repository.WorkOrderEntity;
import com.scaffold.support.repository.WorkOrderRepository;
import com.scaffold.support.security.SupportCurrentUserProvider;
import org.springframework.http.HttpStatus;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class WorkOrderService {

    private static final int MAX_MESSAGE_LENGTH = 4_000;
    private static final int NOT_DELETED = 0;

    private final SupportIntentService intentService;
    private final WorkOrderRepository repository;
    private final SupportCurrentUserProvider currentUserProvider;
    private final Clock clock;
    private final ChatMemory chatMemory;

    public WorkOrderService(SupportIntentService intentService, WorkOrderRepository repository,
                            SupportCurrentUserProvider currentUserProvider, Clock clock, ChatMemory chatMemory) {
        this.intentService = intentService;
        this.repository = repository;
        this.currentUserProvider = currentUserProvider;
        this.clock = clock;
        this.chatMemory = chatMemory;
    }

    @Transactional
    public WorkOrder create(String requestId, String description) {
        long userId = currentUserProvider.requireUserId();
        String validatedRequestId = validateRequestId(requestId);
        String validatedDescription = validateDescription(description);
        return repository.findByUserIdAndRequestIdAndDeleted(userId, validatedRequestId, NOT_DELETED)
                .map(this::toDomain)
                .orElseGet(() -> createNew(userId, validatedRequestId, validatedDescription));
    }

    @Transactional(readOnly = true)
    public List<WorkOrder> listCurrentUserWorkOrders() {
        return repository.findByUserIdAndDeletedOrderByGmtCreatedDescIdDesc(
                        currentUserProvider.requireUserId(), NOT_DELETED).stream()
                .map(this::toDomain)
                .toList();
    }

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

    @Transactional
    public WorkOrder close(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("work order id must be positive");
        }
        long userId = currentUserProvider.requireUserId();
        WorkOrderEntity entity = repository.findByIdAndUserIdAndDeleted(id, userId, NOT_DELETED)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "工单不存在"));
        entity.setStatus(WorkOrderStatus.CLOSED);
        entity.setGmtModified(LocalDateTime.ofInstant(Instant.now(clock), ZoneOffset.UTC));
        entity.setModifiedBy(userId);
        WorkOrder closed = toDomain(repository.saveAndFlush(entity));
        chatMemory.clear(entity.getConversationId());
        return closed;
    }

    private WorkOrder createNew(long userId, String requestId, String description) {
        String conversationId = "work-order:" + UUID.randomUUID();
        WorkOrderIntent intent = intentService.analyze(conversationId, description);
        WorkOrderStatus status = intent.manualReviewRequired()
                ? WorkOrderStatus.MANUAL_REVIEW : WorkOrderStatus.OPEN;
        Instant createdAt = Instant.now(clock);
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
        LocalDateTime timestamp = LocalDateTime.ofInstant(createdAt, ZoneOffset.UTC);
        entity.setGmtCreated(timestamp);
        entity.setGmtModified(timestamp);
        entity.setCreatedBy(userId);
        entity.setModifiedBy(userId);
        return toDomain(repository.saveAndFlush(entity));
    }

    private WorkOrder toDomain(WorkOrderEntity entity) {
        return new WorkOrder(entity.getId(), entity.getUserId(), entity.getRequestId(),
                entity.getConversationId(), entity.getCategory(), entity.getSummary(), entity.getPriority(),
                entity.getStatus(), entity.getOrderNo(), entity.getManualReviewRequired(),
                entity.getOriginalDescription(), entity.getGmtCreated().toInstant(ZoneOffset.UTC));
    }

    private String validateRequestId(String requestId) {
        if (requestId == null || requestId.isBlank()) {
            throw new IllegalArgumentException("requestId must not be blank");
        }
        String normalized = requestId.trim();
        if (!normalized.matches("[A-Za-z0-9_-]{8,100}")) {
            throw new IllegalArgumentException("requestId format is invalid");
        }
        return normalized;
    }

    private String validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }
        String normalized = description.trim();
        if (normalized.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("description must not exceed " + MAX_MESSAGE_LENGTH + " characters");
        }
        return normalized;
    }
}
