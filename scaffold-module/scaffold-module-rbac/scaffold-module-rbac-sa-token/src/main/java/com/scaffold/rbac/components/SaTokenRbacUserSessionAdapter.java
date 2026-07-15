package com.scaffold.rbac.components;

import cn.dev33.satoken.stp.StpUtil;
import com.scaffold.rbac.auth.RbacCurrentUser;
import com.scaffold.rbac.auth.RbacSessionRevoker;
import org.springframework.stereotype.Component;

@Component
public class SaTokenRbacUserSessionAdapter implements RbacCurrentUser, RbacSessionRevoker {

    @Override
    public Long userId() {
        return SaRbacCurrentUser.userId();
    }

    @Override
    public String username() {
        return SaRbacCurrentUser.username();
    }

    @Override
    public void revokeUserSessions(Long userId) {
        if (userId != null) {
            StpUtil.logout(userId);
        }
    }
}
