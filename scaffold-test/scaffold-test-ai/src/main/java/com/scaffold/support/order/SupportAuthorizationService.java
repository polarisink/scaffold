package com.scaffold.support.order;

import com.scaffold.support.order.model.DemoOrder;
import com.scaffold.support.workorder.OrderNotAccessibleException;
import org.springframework.stereotype.Service;

/**
 * 集中校验当前登录用户是否有权访问订单。
 */
@Service
public class SupportAuthorizationService {

    /**
     * 校验订单归属，失败时使用与订单不存在相同的异常。
     */
    public void checkCanView(long currentUserId, DemoOrder order) {
        if (order.getUserId() != currentUserId) {
            // 未授权和订单不存在使用相同异常，避免攻击者枚举其他用户的订单。
            throw new OrderNotAccessibleException();
        }
    }
}
