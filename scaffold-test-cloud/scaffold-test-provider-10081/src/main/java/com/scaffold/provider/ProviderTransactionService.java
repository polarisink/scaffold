package com.scaffold.provider;

import com.scaffold.cloud.seata.TransactionRecordResponse;
import lombok.RequiredArgsConstructor;
import org.apache.seata.core.context.RootContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProviderTransactionService {

    private final ProviderTransactionRecordRepository repository;

    @Transactional(rollbackFor = Exception.class)
    public TransactionRecordResponse create(String businessKey) {
        String xid = requireGlobalTransaction();
        repository.saveAndFlush(new ProviderTransactionRecord(businessKey, xid, "provider branch committed"));
        return new TransactionRecordResponse("provider", businessKey, xid, 1);
    }

    public long count(String businessKey) {
        return repository.countByBusinessKey(businessKey);
    }

    private String requireGlobalTransaction() {
        String xid = RootContext.getXID();
        if (xid == null || xid.isBlank()) {
            throw new IllegalStateException("Provider 未收到 Seata XID");
        }
        return xid;
    }
}
