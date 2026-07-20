package com.scaffold.support.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkOrderIntentTest {

    @Test
    void normalizesOptionalOrderNumber() {
        WorkOrderIntent intent = new WorkOrderIntent(WorkOrderCategory.ORDER_QUERY,
                "查询订单", 2, " 202607190001 ", false);

        assertThat(intent.orderNo()).isEqualTo("202607190001");
    }

    @Test
    void rejectsInvalidStructuredResults() {
        assertThatThrownBy(() -> new WorkOrderIntent(WorkOrderCategory.REFUND,
                "退款", 8, "202607190001", true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("priority");
        assertThatThrownBy(() -> new WorkOrderIntent(WorkOrderCategory.ORDER_QUERY,
                "查询订单", 2, "非法订单号!", false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("orderNo");
    }
}
