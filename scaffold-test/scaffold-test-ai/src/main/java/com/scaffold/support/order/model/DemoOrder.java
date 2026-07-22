package com.scaffold.support.order.model;

import com.scaffold.orm.BaseStringAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** 内部演示订单数据；收件手机号等敏感字段禁止通过 AI 工具返回。 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ai_demo_order")
public class DemoOrder extends BaseStringAuditable {

    @Column(nullable = false, unique = true, length = 32)
    private String orderNo;
    @Column(nullable = false)
    private long userId;
    @Column(nullable = false, length = 64)
    private String productId;
    @Column(nullable = false, length = 200)
    private String productName;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal paidAmount;
    @Column(nullable = false, length = 32)
    private String orderStatus;
    @Column(nullable = false, length = 32)
    private String afterSaleStatus;
    @Column(length = 32)
    private String receiverPhone;
}
