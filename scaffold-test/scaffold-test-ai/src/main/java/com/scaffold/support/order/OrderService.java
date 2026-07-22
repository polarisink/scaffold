package com.scaffold.support.order;

import com.scaffold.support.order.model.*;
import com.scaffold.support.workorder.OrderNotAccessibleException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 查询演示订单、物流和商品信息，并在返回数据前执行当前用户授权校验。
 */
@Service
public class OrderService {

    private final DemoOrderRepository repository;
    private final DemoLogisticsRepository logisticsRepository;
    private final SupportAuthorizationService authorizationService;

    public OrderService(DemoOrderRepository repository, DemoLogisticsRepository logisticsRepository,
                        SupportAuthorizationService authorizationService) {
        this.repository = repository;
        this.logisticsRepository = logisticsRepository;
        this.authorizationService = authorizationService;
    }

    /**
     * 查询当前用户可查看的订单摘要。
     */
    public OrderSummary queryOrder(String orderNo, long currentUserId) {
        return OrderSummary.from(requireAccessibleOrder(orderNo, currentUserId));
    }

    /**
     * 查询当前用户订单对应的物流摘要。
     */
    public LogisticsSummary queryLogistics(String orderNo, long currentUserId) {
        requireAccessibleOrder(orderNo, currentUserId);
        DemoLogistics logistics = logisticsRepository.findByOrderNo(orderNo)
                .orElseThrow(OrderNotAccessibleException::new);
        return LogisticsSummary.from(logistics);
    }

    /**
     * 查询当前用户订单中的商品摘要。
     */
    public ProductSummary queryProduct(String orderNo, long currentUserId) {
        return ProductSummary.from(requireAccessibleOrder(orderNo, currentUserId));
    }

    /**
     * 重新校验订单归属、状态和金额后执行退款；调用方必须已经完成用户二次确认。
     */
    @Transactional
    public OrderSummary executeRefund(String orderNo, long currentUserId, BigDecimal expectedAmount) {
        DemoOrder order = repository.findByOrderNoForUpdate(orderNo)
                .orElseThrow(OrderNotAccessibleException::new);
        authorizationService.checkCanView(currentUserId, order);
        if (order.getPaidAmount().compareTo(expectedAmount) != 0) {
            throw new IllegalStateException("订单退款金额已变化，请重新确认");
        }
        if ("REFUNDED".equals(order.getAfterSaleStatus())) {
            throw new IllegalStateException("订单已完成退款");
        }
        if (!("PAID".equals(order.getOrderStatus()) || "DELIVERED".equals(order.getOrderStatus()))) {
            throw new IllegalStateException("当前订单状态不允许退款");
        }
        if (!"NONE".equals(order.getAfterSaleStatus())) {
            throw new IllegalStateException("订单已有进行中的售后操作");
        }
        order.setAfterSaleStatus("REFUNDED");
        return OrderSummary.from(repository.saveAndFlush(order));
    }

    private DemoOrder requireAccessibleOrder(String orderNo, long currentUserId) {
        DemoOrder order = repository.findByOrderNo(orderNo)
                .orElseThrow(OrderNotAccessibleException::new);
        authorizationService.checkCanView(currentUserId, order);
        return order;
    }
}
