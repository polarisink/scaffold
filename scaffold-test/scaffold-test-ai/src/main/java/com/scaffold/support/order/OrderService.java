package com.scaffold.support.order;

import com.scaffold.support.order.model.DemoLogistics;
import com.scaffold.support.order.model.DemoOrder;
import com.scaffold.support.order.model.LogisticsSummary;
import com.scaffold.support.order.model.OrderSummary;
import com.scaffold.support.order.model.ProductSummary;
import org.springframework.stereotype.Service;

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

    public OrderSummary queryOrder(String orderNo, long currentUserId) {
        return OrderSummary.from(requireAccessibleOrder(orderNo, currentUserId));
    }

    public LogisticsSummary queryLogistics(String orderNo, long currentUserId) {
        requireAccessibleOrder(orderNo, currentUserId);
        DemoLogistics logistics = logisticsRepository.findById(orderNo)
                .orElseThrow(OrderNotAccessibleException::new);
        return LogisticsSummary.from(logistics);
    }

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
