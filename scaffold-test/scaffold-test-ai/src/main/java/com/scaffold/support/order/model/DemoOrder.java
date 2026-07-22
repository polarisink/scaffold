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

/**
 * 内部演示订单数据；收件手机号等敏感字段禁止通过 AI 工具返回。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ai_demo_order")
public class DemoOrder extends BaseStringAuditable {

    /**
     * 对外展示和查询的订单号。
     */
    @Column(nullable = false, unique = true, length = 32)
    private String orderNo;
    /**
     * 订单所属用户，仅用于服务端鉴权。
     */
    @Column(nullable = false)
    private long userId;
    /**
     * 商品业务标识。
     */
    @Column(nullable = false, length = 64)
    private String productId;
    /**
     * 商品展示名称。
     */
    @Column(nullable = false, length = 200)
    private String productName;
    /**
     * 订单实际支付金额。
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal paidAmount;
    /**
     * 当前订单状态。
     */
    @Column(nullable = false, length = 32)
    private String orderStatus;
    /**
     * 当前售后处理状态。
     */
    @Column(nullable = false, length = 32)
    private String afterSaleStatus;
    /**
     * 收件手机号等敏感信息，不允许通过 AI 工具返回。
     */
    @Column(length = 32)
    private String receiverPhone;
}
