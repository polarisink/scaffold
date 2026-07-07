package com.scaffold.rbac.components;

import com.mzt.logapi.beans.Operator;
import com.mzt.logapi.service.IOperatorGetService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class SaTokenServletOperatorGetService implements IOperatorGetService {
    @Override
    public Operator getUser() {
        String username = SaRbacCurrentUser.username();
        return new Operator(username == null ? "anonymous" : username);
    }
}
