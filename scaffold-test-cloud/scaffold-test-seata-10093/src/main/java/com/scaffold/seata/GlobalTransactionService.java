package com.scaffold.seata;

import java.util.Map;

import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;

@Service
public class GlobalTransactionService {

    @GlobalTransactional(name = "test-cloud-seata-transaction", rollbackFor = Exception.class)
    public Map<String, String> execute(boolean fail) {
        if (fail) {
            throw new IllegalStateException("Seata 全局事务示例触发回滚");
        }
        return Map.of("status", "committed");
    }
}
