package com.scaffold.support.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Objects;

/** A work order whose identity, owner, state and timestamps are controlled by Java. */
@Schema(description = "由 Java 控制并持久化的售后工单")
public record WorkOrder(
        @Schema(description = "工单ID", example = "1") Long id,
        @Schema(description = "工单所属用户ID，由服务端身份生成", example = "1001") Long userId,
        @Schema(description = "幂等请求标识", example = "request_0001") String requestId,
        @Schema(description = "服务端生成的AI会话标识") String conversationId,
        @Schema(description = "工单类别", example = "REFUND") WorkOrderCategory category,
        @Schema(description = "工单摘要") String summary,
        @Schema(description = "优先级，范围1到5", minimum = "1", maximum = "5") int priority,
        @Schema(description = "由Java控制的工单状态", example = "MANUAL_REVIEW") WorkOrderStatus status,
        @Schema(description = "关联订单号；用户未提供时为空") String orderNo,
        @Schema(description = "是否需要人工审核") boolean manualReviewRequired,
        @Schema(description = "用户提交的原始售后描述") String originalDescription,
        @Schema(description = "服务端生成的工单创建时间") Instant createdAt) {

    public WorkOrder {
        requirePositive(id, "id");
        requirePositive(userId, "userId");
        requireText(requestId, "requestId");
        requireText(conversationId, "conversationId");
        Objects.requireNonNull(category, "category must not be null");
        requireText(summary, "summary");
        if (priority < 1 || priority > 5) {
            throw new IllegalArgumentException("priority must be between 1 and 5");
        }
        Objects.requireNonNull(status, "status must not be null");
        requireText(originalDescription, "originalDescription");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    private static void requirePositive(Long value, String name) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(name + " must be positive");
        }
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
