package com.scaffold.support.workorder;

/**
 * 订单不存在或不属于当前用户时统一抛出的异常，避免泄露订单是否存在。
 */
public class OrderNotAccessibleException extends IllegalArgumentException {

    /**
     * 创建不暴露具体鉴权失败原因的订单访问异常。
     */
    public OrderNotAccessibleException() {
        super("No order accessible to the current user was found");
    }
}
