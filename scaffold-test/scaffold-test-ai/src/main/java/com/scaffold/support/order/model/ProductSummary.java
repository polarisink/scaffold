package com.scaffold.support.order.model;

/** 可安全暴露给大模型的商品摘要。 */
public record ProductSummary(String productId, String productName) {

    public static ProductSummary from(DemoOrder order) {
        return new ProductSummary(order.getProductId(), order.getProductName());
    }
}
