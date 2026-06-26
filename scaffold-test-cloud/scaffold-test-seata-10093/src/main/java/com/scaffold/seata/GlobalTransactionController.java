package com.scaffold.seata;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class GlobalTransactionController {

    private final GlobalTransactionService globalTransactionService;

    GlobalTransactionController(GlobalTransactionService globalTransactionService) {
        this.globalTransactionService = globalTransactionService;
    }

    @GetMapping("/api/seata/transaction")
    Map<String, String> execute(@RequestParam(name = "fail", defaultValue = "false") boolean fail) {
        return globalTransactionService.execute(fail);
    }
}
