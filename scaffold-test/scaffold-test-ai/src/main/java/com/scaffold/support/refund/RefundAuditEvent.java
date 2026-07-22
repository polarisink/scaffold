package com.scaffold.support.refund;

/**
 * 退款确认流程中需要留痕的安全事件。
 */
public enum RefundAuditEvent {
    PREPARED,
    CONFIRMED,
    EXECUTED,
    CANCELLED,
    EXPIRED
}
