package com.scaffold.support.refund;

/**
 * 退款确认令牌不可用或当前状态不允许操作。
 */
public class RefundActionException extends IllegalStateException {

    public RefundActionException(String message) {
        super(message);
    }
}
