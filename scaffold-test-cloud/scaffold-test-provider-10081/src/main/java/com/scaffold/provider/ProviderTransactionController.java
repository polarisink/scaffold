package com.scaffold.provider;

import com.scaffold.cloud.seata.TransactionRecordResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seata/records")
@RequiredArgsConstructor
@Tag(name = "提供者")
class ProviderTransactionController {

    private final ProviderTransactionService transactionService;

    @Operation(summary = "创建")
    @PostMapping
    public TransactionRecordResponse create(@RequestParam String businessKey) {
        return transactionService.create(businessKey);
    }

    @Operation(summary = "计数")
    @GetMapping("/count")
    public long count(@RequestParam String businessKey) {
        return transactionService.count(businessKey);
    }
}
