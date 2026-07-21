package com.scaffold.support.order;

import com.scaffold.support.order.model.DemoLogistics;
import com.scaffold.support.order.model.DemoOrder;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DemoOrderDataConfiguration {

    @Bean
    ApplicationRunner demoOrderInitializer(DemoOrderRepository orders, DemoLogisticsRepository logistics) {
        return arguments -> {
            saveIfMissing(orders, new DemoOrder("202607190001", 1, "PHONE-X1", "Scaffold Phone X1",
                    new BigDecimal("3999.00"), "DELIVERED", "NONE", "13800000001"));
            saveIfMissing(orders, new DemoOrder("202607190002", 2, "TABLET-PRO", "Scaffold Tablet Pro",
                    new BigDecimal("2699.00"), "SHIPPED", "NONE", "13800000002"));
            saveIfMissing(orders, new DemoOrder("202607190003", 1, "WATCH-S", "Scaffold Watch S",
                    new BigDecimal("899.00"), "PAID", "NONE", "13800000003"));
            if (!logistics.existsById("202607190001")) {
                logistics.save(new DemoLogistics("202607190001", "顺丰速运", "SF-DEMO-0001", "DELIVERED",
                        "快件已由本人签收", LocalDateTime.of(2026, 7, 19, 16, 30)));
            }
            if (!logistics.existsById("202607190002")) {
                logistics.save(new DemoLogistics("202607190002", "京东物流", "JD-DEMO-0002", "IN_TRANSIT",
                        "快件正在运往上海分拨中心", LocalDateTime.of(2026, 7, 20, 9, 15)));
            }
        };
    }

    private void saveIfMissing(DemoOrderRepository repository, DemoOrder order) {
        if (!repository.existsById(order.getOrderNo())) {
            repository.save(order);
        }
    }
}
