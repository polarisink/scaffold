package com.scaffold.remote;

import com.scaffold.base.util.R;
import com.scaffold.cloud.seata.TransactionRecordResponse;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/api/seata/records")
public interface OrderTransactionClient {

    @PostExchange
    R<TransactionRecordResponse> create(@RequestParam String businessKey, @RequestHeader("TX_XID") String xid);

    @GetExchange("/count")
    R<Long> count(@RequestParam String businessKey);
}
