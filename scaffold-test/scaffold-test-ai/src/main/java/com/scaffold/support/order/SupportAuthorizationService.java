package com.scaffold.support.order;

import com.scaffold.support.order.model.DemoOrder;
import org.springframework.stereotype.Service;

@Service
public class SupportAuthorizationService {

    public void checkCanView(long currentUserId, DemoOrder order) {
        if (order.getUserId() != currentUserId) {
            // Deliberately indistinguishable from an absent order to prevent enumeration.
            throw new OrderNotAccessibleException();
        }
    }
}
