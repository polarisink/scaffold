package com.scaffold.support.order.model;

import com.scaffold.orm.BaseStringAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 用于 Tool Calling 演示的物流事实实体。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ai_demo_logistics")
public class DemoLogistics extends BaseStringAuditable {

    /**
     * 物流记录关联的订单号。
     */
    @Column(nullable = false, unique = true, length = 32)
    private String orderNo;
    /**
     * 承运商名称。
     */
    @Column(nullable = false, length = 64)
    private String carrier;
    /**
     * 物流跟踪单号。
     */
    @Column(nullable = false, length = 64)
    private String trackingNo;
    /**
     * 当前物流状态。
     */
    @Column(nullable = false, length = 32)
    private String status;
    /**
     * 最近一次物流节点说明。
     */
    @Column(nullable = false, length = 500)
    private String latestDescription;
    /**
     * 最近一次物流更新时间。
     */
    @Column(nullable = false)
    private LocalDateTime latestUpdateTime;
}
