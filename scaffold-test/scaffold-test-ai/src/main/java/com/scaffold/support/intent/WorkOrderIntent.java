package com.scaffold.support.intent;

import com.scaffold.support.workorder.WorkOrderCategory;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

/** 用户售后描述经过结构化解析和校验后形成的工单意图。 */
@Schema(description = "大模型从售后描述中提取并经 Java 校验的工单意图")
public record WorkOrderIntent(
        @Schema(description = "工单类别", example = "REFUND") WorkOrderCategory category,
        @Schema(description = "忠实概括用户问题的摘要", example = "手机无法开机，用户申请退款") String summary,
        @Schema(description = "优先级，范围1到5", minimum = "1", maximum = "5", example = "4") int priority,
        @Schema(description = "用户明确提供的订单号；未提供时为空", example = "202607190001") String orderNo,
        @Schema(description = "是否需要人工审核", example = "true") boolean manualReviewRequired) {

    public WorkOrderIntent {
        Objects.requireNonNull(category, "category must not be null");
        if (summary == null || summary.isBlank()) {
            throw new IllegalArgumentException("summary must not be blank");
        }
        if (priority < 1 || priority > 5) {
            throw new IllegalArgumentException("priority must be between 1 and 5");
        }
        orderNo = normalizeOrderNo(orderNo);
    }

    private static String normalizeOrderNo(String orderNo) {
        if (orderNo == null || orderNo.isBlank()) {
            return null;
        }
        String normalized = orderNo.trim();
        if (!normalized.matches("[A-Za-z0-9-]{6,32}")) {
            throw new IllegalArgumentException("orderNo format is invalid");
        }
        return normalized;
    }
}
