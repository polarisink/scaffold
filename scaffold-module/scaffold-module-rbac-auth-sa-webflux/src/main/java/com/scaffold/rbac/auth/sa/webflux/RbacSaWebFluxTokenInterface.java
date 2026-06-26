package com.scaffold.rbac.auth.sa.webflux;

import cn.dev33.satoken.stp.StpInterface;
import com.scaffold.rbac.auth.RbacAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RbacSaWebFluxTokenInterface implements StpInterface {
    private final RbacAccountService accountService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return List.of();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return accountService.selectRoleCodeList(Long.valueOf(loginId.toString()));
    }
}
