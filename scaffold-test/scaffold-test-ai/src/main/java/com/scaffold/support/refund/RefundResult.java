package com.scaffold.support.refund;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 用户确认后的退款执行结果。
 */
public record RefundResult(String confirmationId, String orderNo, BigDecimal amount,
                           PendingActionStatus status, Instant executedAt) {
}
