package com.scaffold.rbac.components;

import com.mzt.logapi.beans.Operator;
import com.mzt.logapi.service.IOperatorGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogOperatorServiceImpl implements IOperatorGetService {
    private final RbacProperties rbacProperties;

    @Override
    public Operator getUser() {
        Long userId = SaRbacCurrentUser.userId();
        return userId == null ? new Operator(rbacProperties.getAnonymousUsername()) : new Operator(userId.toString());
    }
}