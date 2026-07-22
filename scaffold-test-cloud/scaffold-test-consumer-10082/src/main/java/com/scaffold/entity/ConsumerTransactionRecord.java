package com.scaffold.entity;

import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.*;

@Entity
@Table(name = "consumer_tx_record")
public class ConsumerTransactionRecord extends BaseAuditable {

    @Column(name = "business_key", nullable = false, unique = true, length = 64)
    private String businessKey;

    @Column(nullable = false, length = 128)
    private String xid;

    @Column(nullable = false)
    private String description;

    protected ConsumerTransactionRecord() {
    }

    public ConsumerTransactionRecord(String businessKey, String xid, String description) {
        this.businessKey = businessKey;
        this.xid = xid;
        this.description = description;
    }
}
