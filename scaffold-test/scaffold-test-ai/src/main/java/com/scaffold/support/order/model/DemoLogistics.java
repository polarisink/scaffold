package com.scaffold.support.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ai_demo_logistics")
public class DemoLogistics {

    @Id
    @Column(name = "order_no", length = 32)
    private String orderNo;
    @Column(nullable = false, length = 64)
    private String carrier;
    @Column(name = "tracking_no", nullable = false, length = 64)
    private String trackingNo;
    @Column(nullable = false, length = 32)
    private String status;
    @Column(name = "latest_description", nullable = false, length = 500)
    private String latestDescription;
    @Column(name = "latest_update_time", nullable = false)
    private LocalDateTime latestUpdateTime;
}
