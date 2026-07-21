package com.scaffold.support.order;

import com.scaffold.support.workorder.OrderNotAccessibleException;

import com.scaffold.support.order.model.DemoLogistics;
import com.scaffold.support.order.model.DemoOrder;
import com.scaffold.support.order.model.LogisticsSummary;
import com.scaffold.support.order.model.OrderSummary;
import com.scaffold.support.order.model.ProductSummary;
import org.springframework.stereotype.Service;

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

    /** 查询当前用户可查看的订单摘要。 */
    public OrderSummary queryOrder(String orderNo, long currentUserId) {
        return OrderSummary.from(requireAccessibleOrder(orderNo, currentUserId));
    }

    /** 查询当前用户订单对应的物流摘要。 */
    public LogisticsSummary queryLogistics(String orderNo, long currentUserId) {
        requireAccessibleOrder(orderNo, currentUserId);
        DemoLogistics logistics = logisticsRepository.findById(orderNo)
                .orElseThrow(OrderNotAccessibleException::new);
        return LogisticsSummary.from(logistics);
    }

    /** 查询当前用户订单中的商品摘要。 */
    public ProductSummary queryProduct(String orderNo, long currentUserId) {
        return ProductSummary.from(requireAccessibleOrder(orderNo, currentUserId));
    }

    private DemoOrder requireAccessibleOrder(String orderNo, long currentUserId) {
        DemoOrder order = repository.findById(orderNo)
                .orElseThrow(OrderNotAccessibleException::new);
        authorizationService.checkCanView(currentUserId, order);
        return order;
    }
}
