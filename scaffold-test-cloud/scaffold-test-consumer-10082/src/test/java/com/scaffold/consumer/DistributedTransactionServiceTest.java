package com.scaffold.consumer;

import com.scaffold.base.util.R;
import com.scaffold.cloud.seata.TransactionRecordResponse;
import com.scaffold.entity.ConsumerTransactionRecord;
import com.scaffold.remote.OrderTransactionClient;
import com.scaffold.remote.ProviderTransactionClient;
import com.scaffold.repo.ConsumerTransactionRecordRepository;
import com.scaffold.service.DistributedTransactionService;
import com.scaffold.vo.DistributedTransactionResult;
import org.apache.seata.core.context.RootContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class DistributedTransactionServiceTest {

    private static final String XID = "127.0.0.1:8091:123456";
    private static final String BUSINESS_KEY = "tx-test-001";

    @Mock
    private ConsumerTransactionRecordRepository consumerRepository;
    @Mock
    private ProviderTransactionClient providerClient;
    @Mock
    private OrderTransactionClient orderClient;

    private DistributedTransactionService transactionService;

    @BeforeEach
    void setUp() {
        RootContext.bind(XID);
        transactionService = new DistributedTransactionService(
                consumerRepository,
                providerClient,
                orderClient);
        when(consumerRepository.saveAndFlush(any(ConsumerTransactionRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(providerClient.create(BUSINESS_KEY, XID)).thenReturn(R.success(
                new TransactionRecordResponse("provider", BUSINESS_KEY, XID, 1)));
        when(orderClient.create(BUSINESS_KEY, XID)).thenReturn(R.success(
                new TransactionRecordResponse("order", BUSINESS_KEY, XID, 1)));
    }

    @AfterEach
    void tearDown() {
        RootContext.unbind();
    }

    @Test
    void shouldWriteAllThreeBranches() {
        DistributedTransactionResult result = transactionService.execute(BUSINESS_KEY, false);

        assertEquals(XID, result.xid());
        assertEquals(1, result.consumer().affectedRows());
        assertEquals(1, result.provider().affectedRows());
        assertEquals(1, result.order().affectedRows());
    }

    @Test
    void shouldThrowAfterAllThreeBranchesWhenRollbackIsRequested() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> transactionService.execute(BUSINESS_KEY, true));

        assertEquals("模拟异常：三个数据库均已写入，触发 Seata 全局回滚", exception.getMessage());
        verify(consumerRepository).saveAndFlush(any(ConsumerTransactionRecord.class));
        verify(providerClient).create(BUSINESS_KEY, XID);
        verify(orderClient).create(BUSINESS_KEY, XID);
    }
}
