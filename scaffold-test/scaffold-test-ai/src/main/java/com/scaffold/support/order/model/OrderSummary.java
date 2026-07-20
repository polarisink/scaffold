package com.scaffold.support.order.model;

import java.math.BigDecimal;

/** Safe order projection exposed to the model. */
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
