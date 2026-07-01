package com.scaffold.provider;

import com.scaffold.cloud.seata.TransactionRecordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seata/records")
@RequiredArgsConstructor
class ProviderTransactionController {

    private final ProviderTransactionService transactionService;

    @PostMapping
    public TransactionRecordResponse create(@RequestParam String businessKey) {
        return transactionService.create(businessKey);
    }

    @GetMapping("/count")
    public long count(@RequestParam String businessKey) {
        return transactionService.count(businessKey);
    }
}
