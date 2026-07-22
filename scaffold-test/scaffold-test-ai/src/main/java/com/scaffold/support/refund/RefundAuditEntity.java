package com.scaffold.support.refund;

import com.scaffold.orm.BaseLongAuditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 退款准备、确认和执行的不可省略业务审计记录。
 */
@Getter
@Setter
@Entity
@Table(name = "ai_refund_audit")
public class RefundAuditEntity extends BaseLongAuditable {

    /**
     * 关联的退款确认标识。
     */
    @Column(nullable = false, length = 64)
    private String confirmationId;
    /**
     * 触发审计事件的用户。
     */
    @Column(nullable = false)
    private Long userId;
    /**
     * 关联订单号。
     */
    @Column(nullable = false, length = 32)
    private String orderNo;
    /**
     * 退款操作生命周期事件。
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private RefundAuditEvent event;
    /**
     * 便于追踪的事件说明。
     */
    @Column(nullable = false, length = 500)
    private String detail;
}
