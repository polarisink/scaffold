package com.scaffold.order;

import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.*;

@Entity
@Table(name = "order_tx_record")
public class OrderTransactionRecord extends BaseAuditable {

    @Column(name = "business_key", nullable = false, unique = true, length = 64)
    private String businessKey;

    @Column(nullable = false, length = 128)
    private String xid;

    @Column(nullable = false)
    private String description;

    protected OrderTransactionRecord() {
    }

    OrderTransactionRecord(String businessKey, String xid, String description) {
        this.businessKey = businessKey;
        this.xid = xid;
        this.description = description;
    }
}
