package com.scaffold.rbac.components;

import com.mzt.logapi.beans.Operator;
import com.mzt.logapi.service.IOperatorGetService;
import com.scaffold.security.vo.LoginUser;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class SecurityServletOperatorGetService implements IOperatorGetService {
    @Override
    public Operator getUser() {
        LoginUser user = LoginUser.currentUser();
        return new Operator(user == null ? "anonymous" : user.getUsername());
    }
}
