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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ai_demo_logistics")
public class DemoLogistics extends BaseStringAuditable {

    @Column(nullable = false, unique = true, length = 32)
    private String orderNo;
    @Column(nullable = false, length = 64)
    private String carrier;
    @Column(nullable = false, length = 64)
    private String trackingNo;
    @Column(nullable = false, length = 32)
    private String status;
    @Column(nullable = false, length = 500)
    private String latestDescription;
    @Column(nullable = false)
    private LocalDateTime latestUpdateTime;
}
