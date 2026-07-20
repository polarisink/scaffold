package com.scaffold.support.repository;

import com.scaffold.support.model.WorkOrderCategory;
import com.scaffold.support.model.WorkOrderStatus;
import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ai_support_work_order", uniqueConstraints =
        @UniqueConstraint(name = "uk_ai_work_order_user_request", columnNames = {"user_id", "request_id"}))
public class WorkOrderEntity extends BaseAuditable {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "request_id", nullable = false, length = 100)
    private String requestId;

    @Column(name = "conversation_id", nullable = false, unique = true, length = 64)
    private String conversationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private WorkOrderCategory category;

    @Column(nullable = false, length = 1_000)
    private String summary;

    @Column(nullable = false)
    private Integer priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private WorkOrderStatus status;

    @Column(name = "order_no", length = 32)
    private String orderNo;

    @Column(name = "manual_review_required", nullable = false)
    private Boolean manualReviewRequired;

    @Column(name = "original_description", nullable = false, length = 4_000)
    private String originalDescription;
}
