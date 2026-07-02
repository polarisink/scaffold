package com.scaffold.rbac.components;

import com.mzt.logapi.beans.Operator;
import com.mzt.logapi.service.IOperatorGetService;
import org.springframework.stereotype.Component;

@Component
public class LogOperatorServiceImpl implements IOperatorGetService {

    @Override
    public Operator getUser() {
        Long userId = SaRbacCurrentUser.userId();
        return userId == null ? new Operator("systemId") : new Operator(userId.toString());
    }
}