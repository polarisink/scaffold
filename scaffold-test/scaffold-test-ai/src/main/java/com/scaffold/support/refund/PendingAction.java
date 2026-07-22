package com.scaffold.support.refund;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 返回给用户确认的退款操作快照。
 */
public record PendingAction(String confirmationId, Long userId, ActionType action,
                            String orderNo, BigDecimal amount, String reason, String summary,
                            Instant expiresAt, PendingActionStatus status) {
}
