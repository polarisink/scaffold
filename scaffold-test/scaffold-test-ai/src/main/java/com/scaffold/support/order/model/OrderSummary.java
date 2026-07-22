package com.scaffold.support.order.model;

import java.math.BigDecimal;

/**
 * 可安全暴露给大模型的订单摘要，不包含收件手机号等敏感字段。
 */
public record OrderSummary(
        String orderNo,
        String productName,
        BigDecimal paidAmount,
        String orderStatus,
        String afterSaleStatus) {

    public static OrderSummary from(DemoOrder order) {
        return new OrderSummary(order.getOrderNo(), order.getProductName(), order.getPaidAmount(),
                order.getOrderStatus(), order.getAfterSaleStatus());
    }
}
