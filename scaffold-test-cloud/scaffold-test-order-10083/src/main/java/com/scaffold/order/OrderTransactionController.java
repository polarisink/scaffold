package com.scaffold.order;

import com.scaffold.cloud.seata.TransactionRecordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seata/records")
@RequiredArgsConstructor
class OrderTransactionController {

    private final OrderTransactionService transactionService;

    @PostMapping
    TransactionRecordResponse create(@RequestParam String businessKey) {
        return transactionService.create(businessKey);
    }

    @GetMapping("/count")
    long count(@RequestParam String businessKey) {
        return transactionService.count(businessKey);
    }
}
