package com.scaffold.support.order;

import com.scaffold.support.order.model.DemoOrder;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** 验证退款执行前的订单归属、金额和状态复核。 */
class OrderServiceRefundTest {

    @Test
    void rechecksAmountAndBusinessStateBeforeRefund() {
        DemoOrderRepository repository = mock(DemoOrderRepository.class);
        DemoOrder order = order();
        when(repository.findByOrderNoForUpdate("202607190001")).thenReturn(Optional.of(order));
        when(repository.saveAndFlush(order)).thenReturn(order);
        OrderService service = new OrderService(repository, mock(DemoLogisticsRepository.class),
                new SupportAuthorizationService());

        var result = service.executeRefund("202607190001", 1001L, new BigDecimal("3999.00"));

        assertThat(result.afterSaleStatus()).isEqualTo("REFUNDED");
        verify(repository).saveAndFlush(order);
    }

    @Test
    void rejectsRefundWhenConfirmedAmountNoLongerMatches() {
        DemoOrderRepository repository = mock(DemoOrderRepository.class);
        DemoOrder order = order();
        when(repository.findByOrderNoForUpdate("202607190001")).thenReturn(Optional.of(order));
        OrderService service = new OrderService(repository, mock(DemoLogisticsRepository.class),
                new SupportAuthorizationService());

        assertThatThrownBy(() -> service.executeRefund("202607190001", 1001L, new BigDecimal("1.00")))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("金额已变化");
        verify(repository, never()).saveAndFlush(order);
    }

    private DemoOrder order() {
        return new DemoOrder("202607190001", 1001L, "PHONE-X", "Scaffold Phone X",
                new BigDecimal("3999.00"), "DELIVERED", "NONE", "13800000001");
    }
}
