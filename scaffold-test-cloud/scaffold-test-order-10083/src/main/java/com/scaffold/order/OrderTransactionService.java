package com.scaffold.order;

import com.scaffold.cloud.seata.TransactionRecordResponse;
import lombok.RequiredArgsConstructor;
import org.apache.seata.core.context.RootContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class OrderTransactionService {

    private final OrderTransactionRecordRepository repository;

    @Transactional(rollbackFor = Exception.class)
    public TransactionRecordResponse create(String businessKey) {
        String xid = requireGlobalTransaction();
        repository.saveAndFlush(new OrderTransactionRecord(businessKey, xid, "order branch committed"));
        return new TransactionRecordResponse("order", businessKey, xid, 1);
    }

    public long count(String businessKey) {
        return repository.countByBusinessKey(businessKey);
    }

    private String requireGlobalTransaction() {
        String xid = RootContext.getXID();
        if (xid == null || xid.isBlank()) {
            throw new IllegalStateException("Order 未收到 Seata XID");
        }
        return xid;
    }
}
