package com.scaffold.support.refund;

import com.scaffold.orm.BaseLongAuditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 退款待确认操作持久化实体。
 */
@Getter
@Setter
@Entity
@Table(name = "ai_pending_action", uniqueConstraints =
@UniqueConstraint(name = "uk_ai_pending_confirmation", columnNames = "confirmation_id"))
public class PendingActionEntity extends BaseLongAuditable {

    /**
     * 安全随机生成且不可预测的确认标识。
     */
    @Column(nullable = false, length = 64)
    private String confirmationId;
    /**
     * 创建待确认操作的用户。
     */
    @Column(nullable = false)
    private Long userId;
    /**
     * 待确认的业务动作类型。
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ActionType action;
    /**
     * 退款目标订单号。
     */
    @Column(nullable = false, length = 32)
    private String orderNo;
    /**
     * 准备阶段冻结的退款金额快照。
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    /**
     * 用户明确提供的退款原因。
     */
    @Column(nullable = false, length = 500)
    private String reason;
    /**
     * 前端确认卡片展示的操作摘要。
     */
    @Column(nullable = false, length = 1000)
    private String summary;
    /**
     * 确认标识失效时间。
     */
    @Column(nullable = false)
    private Instant expiresAt;
    /**
     * 待确认操作的当前状态。
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PendingActionStatus status;
    /**
     * 退款实际执行时间，尚未执行时为空。
     */
    private Instant executedAt;
}
