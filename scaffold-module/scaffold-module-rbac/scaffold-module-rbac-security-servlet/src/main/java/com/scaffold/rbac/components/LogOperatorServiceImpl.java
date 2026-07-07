package com.scaffold.rbac.components;

import com.mzt.logapi.beans.Operator;
import com.mzt.logapi.service.IOperatorGetService;
import com.scaffold.security.vo.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogOperatorServiceImpl implements IOperatorGetService {
    private final RbacProperties rbacProperties;

    @Override
    public Operator getUser() {
        Long userId = LoginUser.userId();
        return userId == null ? new Operator(rbacProperties.getAnonymousUsername()) : new Operator(userId.toString());
    }
}