package com.scaffold.service;

import com.scaffold.base.util.R;
import com.scaffold.cloud.seata.TransactionRecordResponse;
import com.scaffold.entity.ConsumerTransactionRecord;
import com.scaffold.remote.OrderTransactionClient;
import com.scaffold.remote.ProviderTransactionClient;
import com.scaffold.repo.ConsumerTransactionRecordRepository;
import com.scaffold.vo.DistributedTransactionResult;
import com.scaffold.vo.TransactionRecordCounts;
import lombok.RequiredArgsConstructor;
import org.apache.seata.core.context.RootContext;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class DistributedTransactionService {

    private final ConsumerTransactionRecordRepository consumerRepository;
    private final ProviderTransactionClient providerClient;
    private final OrderTransactionClient orderClient;

    @GlobalTransactional(name = "consumer-provider-order-demo", rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    public DistributedTransactionResult execute(String businessKey, boolean fail) {
        validateBusinessKey(businessKey);
        String xid = requireGlobalTransaction();

        consumerRepository.saveAndFlush(new ConsumerTransactionRecord(businessKey, xid, "consumer global transaction"));
        TransactionRecordResponse consumer = new TransactionRecordResponse("consumer", businessKey, xid, 1);
        TransactionRecordResponse provider = requireSuccessfulBranch("provider", providerClient.create(businessKey, xid));
        TransactionRecordResponse order = requireSuccessfulBranch("order", orderClient.create(businessKey, xid));

        if (fail) {
            throw new IllegalStateException("模拟异常：三个数据库均已写入，触发 Seata 全局回滚");
        }

        return new DistributedTransactionResult(businessKey, xid, consumer, provider, order);
    }

    public TransactionRecordCounts counts(String businessKey) {
        validateBusinessKey(businessKey);
        return new TransactionRecordCounts(businessKey, consumerRepository.countByBusinessKey(businessKey), requireCount("provider", providerClient.count(businessKey)), requireCount("order", orderClient.count(businessKey)));
    }

    private TransactionRecordResponse requireSuccessfulBranch(String service, R<TransactionRecordResponse> response) {
        TransactionRecordResponse data = response == null ? null : response.getData();
        if (data == null || data.affectedRows() != 1) {
            throw new IllegalStateException(service + " 分支写入失败");
        }
        return data;
    }

    private long requireCount(String service, R<Long> response) {
        Long count = response == null ? null : response.getData();
        if (count == null) {
            throw new IllegalStateException(service + " 分支查询失败");
        }
        return count;
    }

    private String requireGlobalTransaction() {
        String xid = RootContext.getXID();
        if (xid == null || xid.isBlank()) {
            throw new IllegalStateException("Consumer 未创建 Seata 全局事务");
        }
        return xid;
    }

    private void validateBusinessKey(String businessKey) {
        if (!StringUtils.hasText(businessKey) || businessKey.length() > 64) {
            throw new IllegalArgumentException("businessKey 不能为空且长度不能超过 64");
        }
    }
}
