package com.scaffold.controller;

import com.scaffold.service.DistributedTransactionService;
import com.scaffold.vo.DistributedTransactionResult;
import com.scaffold.vo.TransactionRecordCounts;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seata/transactions")
@RequiredArgsConstructor
class DistributedTransactionController {

    private final DistributedTransactionService transactionService;

    @PostMapping("/{businessKey}/{fail}")
    DistributedTransactionResult execute(@PathVariable String businessKey, @PathVariable boolean fail) {
        return transactionService.execute(businessKey, fail);
    }

    @GetMapping("/{businessKey}/counts")
    TransactionRecordCounts counts(@PathVariable String businessKey) {
        return transactionService.counts(businessKey);
    }
}
