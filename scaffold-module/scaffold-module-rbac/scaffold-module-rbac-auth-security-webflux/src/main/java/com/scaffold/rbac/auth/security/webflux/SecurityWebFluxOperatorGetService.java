package com.scaffold.rbac.auth.security.webflux;

import com.mzt.logapi.beans.Operator;
import com.mzt.logapi.service.IOperatorGetService;
import com.scaffold.rbac.components.RbacProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * bizlog 的操作人接口是同步接口。宿主开启 Reactor 上下文到 ThreadLocal 的传播后，
 * 可从 SecurityContextHolder 取得用户；否则安全降级为 anonymous。
 */
@Primary
@Component
@RequiredArgsConstructor
public class SecurityWebFluxOperatorGetService implements IOperatorGetService {
    private final RbacProperties rbacProperties;
    @Override
    public Operator getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new Operator(rbacProperties.getAnonymousUsername());
        }
        Object credentials = authentication.getCredentials();
        String operator = credentials == null ? authentication.getName() : credentials.toString();
        return new Operator(operator);
    }
}
