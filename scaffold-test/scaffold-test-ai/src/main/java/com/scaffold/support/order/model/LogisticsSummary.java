package com.scaffold.support.order.model;

import java.time.LocalDateTime;

/** 可安全暴露给大模型的物流摘要。 */
public record LogisticsSummary(
        String orderNo,
        String carrier,
        String trackingStatus,
        String latestDescription,
        LocalDateTime latestUpdateTime) {

    public static LogisticsSummary from(DemoLogistics logistics) {
        return new LogisticsSummary(logistics.getOrderNo(), logistics.getCarrier(), logistics.getStatus(),
                logistics.getLatestDescription(), logistics.getLatestUpdateTime());
    }
}
