package com.scaffold.order;

import com.scaffold.cloud.seata.TransactionRecordResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seata/records")
@RequiredArgsConstructor
@Tag(name = "订单")
class OrderTransactionController {

    private final OrderTransactionService transactionService;

    @PostMapping
    @Operation(summary = "创建")
    public TransactionRecordResponse create(@RequestParam String businessKey) {
        return transactionService.create(businessKey);
    }

    @GetMapping("/count")
    @Operation(summary = "计数")
    public long count(@RequestParam String businessKey) {
        return transactionService.count(businessKey);
    }
}
