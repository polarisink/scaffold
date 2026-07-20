package com.scaffold.support.order;

import com.scaffold.support.order.model.DemoOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class DemoOrderRepositoryTest {

    @Autowired
    private DemoOrderRepository repository;

    @Test
    void readsOrderFromDatabase() {
        repository.saveAndFlush(new DemoOrder("DB-ORDER-001", 10L, "PHONE-X1",
                "Scaffold Phone X1", new BigDecimal("3999.00"), "DELIVERED", "NONE", "13800000001"));

        assertThat(repository.findById("DB-ORDER-001"))
                .get()
                .extracting(DemoOrder::getUserId, DemoOrder::getPaidAmount)
                .containsExactly(10L, new BigDecimal("3999.00"));
    }
}
