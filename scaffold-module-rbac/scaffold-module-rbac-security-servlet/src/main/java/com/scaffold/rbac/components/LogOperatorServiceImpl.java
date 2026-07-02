package com.scaffold.rbac.components;

import com.mzt.logapi.beans.Operator;
import com.mzt.logapi.service.IOperatorGetService;
import com.scaffold.security.vo.LoginUser;
import org.springframework.stereotype.Component;

@Component
public class LogOperatorServiceImpl implements IOperatorGetService {

    @Override
    public Operator getUser() {
        Long userId = LoginUser.userId();
        return userId == null ? new Operator("systemId") : new Operator(userId.toString());
    }
}