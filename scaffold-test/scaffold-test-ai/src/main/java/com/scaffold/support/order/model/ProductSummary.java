package com.scaffold.support.order.model;

/** Safe product projection exposed to the model. */
public record ProductSummary(String productId, String productName) {

    public static ProductSummary from(DemoOrder order) {
        return new ProductSummary(order.getProductId(), order.getProductName());
    }
}
