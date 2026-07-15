package com.scaffold.rbac.components;

import com.mzt.logapi.beans.Operator;
import com.mzt.logapi.service.IOperatorGetService;
import com.scaffold.rbac.auth.RbacCurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogOperatorServiceImpl implements IOperatorGetService {
    private final RbacProperties rbacProperties;
    private final RbacCurrentUser rbacCurrentUser;

    @Override
    public Operator getUser() {
        Long userId = rbacCurrentUser.userId();
        return userId == null ? new Operator(rbacProperties.getAnonymousUsername()) : new Operator(userId.toString());
    }
}