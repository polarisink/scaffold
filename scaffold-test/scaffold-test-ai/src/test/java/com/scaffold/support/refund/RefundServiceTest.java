package com.scaffold.support.refund;

import com.scaffold.support.identity.SupportCurrentUserProvider;
import com.scaffold.support.order.OrderService;
import com.scaffold.support.order.model.OrderSummary;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/** 验证退款准备、确认、取消、过期、越权和幂等行为。 */
class RefundServiceTest {

    @Test
    void preparesRefundBoundToCurrentUserAndOrderParameters() {
        PendingActionRepository repository = mock(PendingActionRepository.class);
        RefundAuditRepository audits = mock(RefundAuditRepository.class);
        OrderService orders = mock(OrderService.class);
        SupportCurrentUserProvider users = () -> 1001L;
        Clock clock = Clock.fixed(Instant.parse("2026-07-22T02:00:00Z"), ZoneOffset.UTC);
        when(orders.queryOrder("202607190001", 1001L)).thenReturn(new OrderSummary(
                "202607190001", "Scaffold Phone X", new BigDecimal("3999.00"), "DELIVERED", "NONE"));
        when(repository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));
        RefundService service = new RefundService(repository, audits, orders, users, clock, Duration.ofMinutes(10));

        PendingAction pending = service.prepare(new PrepareRefundRequest(
                "202607190001", "手机无法开机，申请退款"));

        assertThat(pending.confirmationId()).isNotBlank().hasSizeGreaterThanOrEqualTo(32);
        assertThat(pending.userId()).isEqualTo(1001L);
        assertThat(pending.action()).isEqualTo(ActionType.REFUND);
        assertThat(pending.orderNo()).isEqualTo("202607190001");
        assertThat(pending.amount()).isEqualByComparingTo("3999.00");
        assertThat(pending.reason()).isEqualTo("手机无法开机，申请退款");
        assertThat(pending.expiresAt()).isEqualTo(Instant.parse("2026-07-22T02:10:00Z"));
        assertThat(pending.status()).isEqualTo(PendingActionStatus.PENDING);
        verify(repository).saveAndFlush(any(PendingActionEntity.class));
        verify(audits).save(any(RefundAuditEntity.class));
    }

    @Test
    void confirmsRefundAfterRecheckingOrderAndIsIdempotent() {
        PendingActionRepository repository = mock(PendingActionRepository.class);
        RefundAuditRepository audits = mock(RefundAuditRepository.class);
        OrderService orders = mock(OrderService.class);
        SupportCurrentUserProvider users = () -> 1001L;
        Clock clock = Clock.fixed(Instant.parse("2026-07-22T02:05:00Z"), ZoneOffset.UTC);
        PendingActionEntity entity = pendingEntity(PendingActionStatus.PENDING,
                Instant.parse("2026-07-22T02:10:00Z"));
        when(repository.findForUpdate("confirmation-token", 0)).thenReturn(java.util.Optional.of(entity));
        when(repository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(orders.executeRefund("202607190001", 1001L, new BigDecimal("3999.00")))
                .thenReturn(new OrderSummary("202607190001", "Scaffold Phone X",
                        new BigDecimal("3999.00"), "DELIVERED", "REFUNDED"));
        RefundService service = new RefundService(repository, audits, orders, users, clock, Duration.ofMinutes(10));

        RefundResult first = service.confirm("confirmation-token");
        RefundResult repeated = service.confirm("confirmation-token");

        assertThat(first.status()).isEqualTo(PendingActionStatus.CONFIRMED);
        assertThat(repeated).isEqualTo(first);
        verify(orders, times(1)).executeRefund("202607190001", 1001L, new BigDecimal("3999.00"));
        verify(audits, times(2)).save(any(RefundAuditEntity.class));
    }

    @Test
    void rejectsExpiredConfirmationAndPersistsExpiredStatus() {
        PendingActionRepository repository = mock(PendingActionRepository.class);
        RefundAuditRepository audits = mock(RefundAuditRepository.class);
        OrderService orders = mock(OrderService.class);
        PendingActionEntity entity = pendingEntity(PendingActionStatus.PENDING,
                Instant.parse("2026-07-22T02:00:00Z"));
        when(repository.findForUpdate("confirmation-token", 0)).thenReturn(java.util.Optional.of(entity));
        RefundService service = new RefundService(repository, audits, orders, () -> 1001L,
                Clock.fixed(Instant.parse("2026-07-22T02:01:00Z"), ZoneOffset.UTC), Duration.ofMinutes(10));

        assertThatThrownBy(() -> service.confirm("confirmation-token"))
                .isInstanceOf(RefundActionException.class).hasMessageContaining("已过期");

        assertThat(entity.getStatus()).isEqualTo(PendingActionStatus.EXPIRED);
        verify(repository).saveAndFlush(entity);
        verify(audits).save(any(RefundAuditEntity.class));
    }

    @Test
    void rejectsConfirmationOwnedByAnotherUser() {
        PendingActionRepository repository = mock(PendingActionRepository.class);
        RefundAuditRepository audits = mock(RefundAuditRepository.class);
        OrderService orders = mock(OrderService.class);
        PendingActionEntity entity = pendingEntity(PendingActionStatus.PENDING,
                Instant.parse("2026-07-22T02:10:00Z"));
        when(repository.findForUpdate("confirmation-token", 0)).thenReturn(java.util.Optional.of(entity));
        RefundService service = new RefundService(repository, audits, orders, () -> 2002L,
                Clock.fixed(Instant.parse("2026-07-22T02:01:00Z"), ZoneOffset.UTC), Duration.ofMinutes(10));

        assertThatThrownBy(() -> service.confirm("confirmation-token"))
                .isInstanceOf(RefundActionException.class).hasMessageContaining("不存在");
    }

    @Test
    void cancelsPendingConfirmationAndCannotExecuteIt() {
        PendingActionRepository repository = mock(PendingActionRepository.class);
        RefundAuditRepository audits = mock(RefundAuditRepository.class);
        OrderService orders = mock(OrderService.class);
        PendingActionEntity entity = pendingEntity(PendingActionStatus.PENDING,
                Instant.parse("2026-07-22T02:10:00Z"));
        when(repository.findForUpdate("confirmation-token", 0)).thenReturn(java.util.Optional.of(entity));
        when(repository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));
        RefundService service = new RefundService(repository, audits, orders, () -> 1001L,
                Clock.fixed(Instant.parse("2026-07-22T02:01:00Z"), ZoneOffset.UTC), Duration.ofMinutes(10));

        PendingAction cancelled = service.cancel("confirmation-token");

        assertThat(cancelled.status()).isEqualTo(PendingActionStatus.CANCELLED);
        assertThatThrownBy(() -> service.confirm("confirmation-token"))
                .isInstanceOf(RefundActionException.class).hasMessageContaining("不可执行");
    }

    private PendingActionEntity pendingEntity(PendingActionStatus status, Instant expiresAt) {
        PendingActionEntity entity = new PendingActionEntity();
        entity.setConfirmationId("confirmation-token");
        entity.setUserId(1001L);
        entity.setAction(ActionType.REFUND);
        entity.setOrderNo("202607190001");
        entity.setAmount(new BigDecimal("3999.00"));
        entity.setReason("手机无法开机，申请退款");
        entity.setSummary("确认退款");
        entity.setExpiresAt(expiresAt);
        entity.setStatus(status);
        return entity;
    }
}
