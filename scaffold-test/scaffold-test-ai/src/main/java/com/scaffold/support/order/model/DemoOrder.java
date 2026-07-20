package com.scaffold.support.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Internal demo data. Fields such as receiverPhone must never be returned by an AI tool. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ai_demo_order")
public class DemoOrder {

    @Id
    @Column(name = "order_no", length = 32)
    private String orderNo;
    @Column(name = "user_id", nullable = false)
    private long userId;
    @Column(name = "product_id", nullable = false, length = 64)
    private String productId;
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;
    @Column(name = "paid_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal paidAmount;
    @Column(name = "order_status", nullable = false, length = 32)
    private String orderStatus;
    @Column(name = "after_sale_status", nullable = false, length = 32)
    private String afterSaleStatus;
    @Column(name = "receiver_phone", length = 32)
    private String receiverPhone;
}
