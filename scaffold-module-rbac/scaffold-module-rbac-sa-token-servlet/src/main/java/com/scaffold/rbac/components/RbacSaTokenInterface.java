package com.scaffold.rbac.components;

import cn.dev33.satoken.stp.StpInterface;
import com.scaffold.rbac.auth.RbacAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RbacSaTokenInterface implements StpInterface {
    private final RbacAccountService accountService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return accountService.selectPermissionCodeList(toUserId(loginId));
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return accountService.selectRoleCodeList(toUserId(loginId));
    }

    private Long toUserId(Object loginId) {
        if (loginId instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(loginId.toString());
    }
}
