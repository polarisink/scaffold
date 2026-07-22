package com.scaffold.provider;

import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.*;

@Entity
@Table(name = "provider_tx_record")
public class ProviderTransactionRecord extends BaseAuditable {

    @Column(name = "business_key", nullable = false, unique = true, length = 64)
    private String businessKey;

    @Column(nullable = false, length = 128)
    private String xid;

    @Column(nullable = false)
    private String description;

    protected ProviderTransactionRecord() {
    }

    ProviderTransactionRecord(String businessKey, String xid, String description) {
        this.businessKey = businessKey;
        this.xid = xid;
        this.description = description;
    }
}
