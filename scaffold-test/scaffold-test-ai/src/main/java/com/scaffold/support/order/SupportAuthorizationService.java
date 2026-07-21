package com.scaffold.support.order;

import com.scaffold.support.workorder.OrderNotAccessibleException;

import com.scaffold.support.order.model.DemoOrder;
import org.springframework.stereotype.Service;

@Service
public class SupportAuthorizationService {

    public void checkCanView(long currentUserId, DemoOrder order) {
        if (order.getUserId() != currentUserId) {
            // 未授权和订单不存在使用相同异常，避免攻击者枚举其他用户的订单。
            throw new OrderNotAccessibleException();
        }
    }
}
