package com.scaffold.controller;

import com.scaffold.service.DistributedTransactionService;
import com.scaffold.vo.DistributedTransactionResult;
import com.scaffold.vo.TransactionRecordCounts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seata/transactions")
@RequiredArgsConstructor
@Tag(name = "分布式事务")
class DistributedTransactionController {

    private final DistributedTransactionService transactionService;

    @Operation(summary = "业务操作")
    @PostMapping("/{businessKey}/{fail}")
    public DistributedTransactionResult execute(@PathVariable String businessKey, @PathVariable boolean fail) {
        return transactionService.execute(businessKey, fail);
    }

    @Operation(summary = "计数")
    @GetMapping("/{businessKey}/counts")
    public TransactionRecordCounts counts(@PathVariable String businessKey) {
        return transactionService.counts(businessKey);
    }
}
