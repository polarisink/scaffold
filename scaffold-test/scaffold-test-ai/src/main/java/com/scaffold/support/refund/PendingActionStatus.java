package com.scaffold.support.refund;

/**
 * 待确认操作的生命周期状态。
 */
public enum PendingActionStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    EXPIRED
}
