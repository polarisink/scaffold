package com.scaffold.support.refund;

import com.scaffold.support.identity.SupportCurrentUserProvider;
import com.scaffold.support.order.OrderService;
import com.scaffold.support.order.model.OrderSummary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.util.Base64;

/**
 * 创建并管理退款二次确认，准备阶段不会执行退款。
 */
@Service
public class RefundService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final PendingActionRepository repository;
    private final RefundAuditRepository auditRepository;
    private final OrderService orderService;
    private final SupportCurrentUserProvider currentUserProvider;
    private final Clock clock;
    private final Duration confirmationTtl;

    public RefundService(PendingActionRepository repository, RefundAuditRepository auditRepository,
                         OrderService orderService,
                         SupportCurrentUserProvider currentUserProvider, Clock clock,
                         @Value("${scaffold.ai.refund.confirmation-ttl:10m}") Duration confirmationTtl) {
        this.repository = repository;
        this.auditRepository = auditRepository;
        this.orderService = orderService;
        this.currentUserProvider = currentUserProvider;
        this.clock = clock;
        this.confirmationTtl = confirmationTtl;
    }

    /**
     * 根据当前订单快照创建短期有效、不可预测的退款确认令牌。
     */
    @Transactional
    public PendingAction prepare(PrepareRefundRequest request) {
        long userId = currentUserProvider.requireUserId();
        OrderSummary order = orderService.queryOrder(request.orderNo(), userId);
        PendingActionEntity entity = new PendingActionEntity();
        entity.setConfirmationId(newConfirmationId());
        entity.setUserId(userId);
        entity.setAction(ActionType.REFUND);
        entity.setOrderNo(order.orderNo());
        entity.setAmount(order.paidAmount());
        entity.setReason(request.reason().trim());
        entity.setSummary("确认对订单 " + order.orderNo() + " 申请退款 " + order.paidAmount() + " 元");
        entity.setExpiresAt(clock.instant().plus(confirmationTtl));
        entity.setStatus(PendingActionStatus.PENDING);
        PendingAction saved = toDomain(repository.saveAndFlush(entity));
        audit(entity, RefundAuditEvent.PREPARED, "已创建退款待确认操作");
        return saved;
    }

    /**
     * 用户二次确认后执行退款；重复确认返回第一次结果，不会重复执行。
     */
    @Transactional(noRollbackFor = RefundActionException.class)
    public RefundResult confirm(String confirmationId) {
        long userId = currentUserProvider.requireUserId();
        PendingActionEntity entity = repository.findForUpdate(confirmationId, 0)
                .filter(action -> action.getUserId() == userId)
                .orElseThrow(() -> new RefundActionException("退款确认不存在"));
        if (entity.getStatus() == PendingActionStatus.CONFIRMED) {
            return toResult(entity);
        }
        if (entity.getStatus() != PendingActionStatus.PENDING) {
            throw new RefundActionException("退款确认当前状态不可执行：" + entity.getStatus());
        }
        if (!clock.instant().isBefore(entity.getExpiresAt())) {
            entity.setStatus(PendingActionStatus.EXPIRED);
            repository.saveAndFlush(entity);
            audit(entity, RefundAuditEvent.EXPIRED, "确认令牌已过期，拒绝执行");
            throw new RefundActionException("退款确认已过期");
        }
        audit(entity, RefundAuditEvent.CONFIRMED, "用户完成二次确认");
        orderService.executeRefund(entity.getOrderNo(), userId, entity.getAmount());
        entity.setStatus(PendingActionStatus.CONFIRMED);
        entity.setExecutedAt(clock.instant());
        RefundResult result = toResult(repository.saveAndFlush(entity));
        audit(entity, RefundAuditEvent.EXECUTED, "退款已执行");
        return result;
    }

    /**
     * 取消尚未执行的退款确认；取消后不可再次确认。
     */
    @Transactional
    public PendingAction cancel(String confirmationId) {
        long userId = currentUserProvider.requireUserId();
        PendingActionEntity entity = repository.findForUpdate(confirmationId, 0)
                .filter(action -> action.getUserId() == userId)
                .orElseThrow(() -> new RefundActionException("退款确认不存在"));
        if (entity.getStatus() == PendingActionStatus.CANCELLED) {
            return toDomain(entity);
        }
        if (entity.getStatus() != PendingActionStatus.PENDING) {
            throw new RefundActionException("退款确认当前状态不可取消：" + entity.getStatus());
        }
        entity.setStatus(PendingActionStatus.CANCELLED);
        PendingAction cancelled = toDomain(repository.saveAndFlush(entity));
        audit(entity, RefundAuditEvent.CANCELLED, "用户取消退款确认");
        return cancelled;
    }

    private String newConfirmationId() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private PendingAction toDomain(PendingActionEntity entity) {
        return new PendingAction(entity.getConfirmationId(), entity.getUserId(), entity.getAction(),
                entity.getOrderNo(), entity.getAmount(), entity.getReason(), entity.getSummary(),
                entity.getExpiresAt(), entity.getStatus());
    }

    private RefundResult toResult(PendingActionEntity entity) {
        return new RefundResult(entity.getConfirmationId(), entity.getOrderNo(), entity.getAmount(),
                entity.getStatus(), entity.getExecutedAt());
    }

    private void audit(PendingActionEntity action, RefundAuditEvent event, String detail) {
        RefundAuditEntity audit = new RefundAuditEntity();
        audit.setConfirmationId(action.getConfirmationId());
        audit.setUserId(action.getUserId());
        audit.setOrderNo(action.getOrderNo());
        audit.setEvent(event);
        audit.setDetail(detail);
        auditRepository.save(audit);
    }
}
