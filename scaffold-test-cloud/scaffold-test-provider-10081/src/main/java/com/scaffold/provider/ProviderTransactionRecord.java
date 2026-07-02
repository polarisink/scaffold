package com.scaffold.provider;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "provider_tx_record")
public class ProviderTransactionRecord {

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

    protected ProviderTransactionRecord() {
    }

    ProviderTransactionRecord(String businessKey, String xid, String description) {
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
