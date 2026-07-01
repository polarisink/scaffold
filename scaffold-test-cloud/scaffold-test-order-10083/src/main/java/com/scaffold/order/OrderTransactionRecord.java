package com.scaffold.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_tx_record")
public class OrderTransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_key", nullable = false, unique = true, length = 64)
    private String businessKey;

    @Column(nullable = false, length = 128)
    private String xid;

    @Column(nullable = false)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected OrderTransactionRecord() {
    }

    OrderTransactionRecord(String businessKey, String xid, String description) {
        this.businessKey = businessKey;
        this.xid = xid;
        this.description = description;
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
