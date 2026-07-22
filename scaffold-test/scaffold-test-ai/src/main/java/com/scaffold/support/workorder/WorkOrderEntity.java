package com.scaffold.support.workorder;

import com.scaffold.orm.BaseLongAuditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 售后工单持久化实体，审计字段由基类统一自动填充。
 */
@Getter
@Setter
@Entity
@Table(name = "ai_support_work_order", uniqueConstraints =
@UniqueConstraint(name = "uk_ai_work_order_user_request", columnNames = {"user_id", "request_id"}))
public class WorkOrderEntity extends BaseLongAuditable {

    /**
     * 工单所属用户。
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 客户端幂等请求标识。
     */
    @Column(nullable = false, length = 100)
    private String requestId;

    /**
     * 服务端生成的模型会话标识。
     */
    @Column(nullable = false, unique = true, length = 64)
    private String conversationId;

    /**
     * AI 提取并经 Java 校验的工单分类。
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private WorkOrderCategory category;

    /**
     * 工单问题摘要。
     */
    @Column(nullable = false, length = 1_000)
    private String summary;

    /**
     * 工单处理优先级。
     */
    @Column(nullable = false)
    private Integer priority;

    /**
     * Java 状态机维护的工单状态。
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private WorkOrderStatus status;

    /**
     * 用户明确提供的订单号，可为空。
     */
    @Column(length = 32)
    private String orderNo;

    /**
     * 是否必须转人工审核。
     */
    @Column(nullable = false)
    private Boolean manualReviewRequired;

    /**
     * 用户提交的原始售后描述。
     */
    @Column(nullable = false, length = 4_000)
    private String originalDescription;
}
