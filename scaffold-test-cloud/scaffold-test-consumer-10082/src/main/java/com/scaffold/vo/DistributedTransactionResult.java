package com.scaffold.vo;

import com.scaffold.cloud.seata.TransactionRecordResponse;

public record DistributedTransactionResult(
        String businessKey,
        String xid,
        TransactionRecordResponse consumer,
        TransactionRecordResponse provider,
        TransactionRecordResponse order) {
}
