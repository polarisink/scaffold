package com.scaffold.rbac.components;

import cn.dev33.satoken.stp.StpUtil;
import com.mzt.logapi.beans.Operator;
import com.mzt.logapi.service.IOperatorGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
@RequiredArgsConstructor
public class SaTokenWebFluxOperatorGetService implements IOperatorGetService {
    private final RbacProperties rbacProperties;

    @Override
    public Operator getUser() {
        if (!StpUtil.isLogin()) {
            return new Operator(rbacProperties.getAnonymousUsername());
        }
        Object username = StpUtil.getSession().get("username");
        return new Operator(username == null ? StpUtil.getLoginIdAsString() : username.toString());
    }
}
