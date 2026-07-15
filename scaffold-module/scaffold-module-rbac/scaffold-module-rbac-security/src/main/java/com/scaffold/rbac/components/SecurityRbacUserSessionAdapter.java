package com.scaffold.rbac.components;

import com.scaffold.rbac.auth.RbacCurrentUser;
import com.scaffold.rbac.auth.RbacSessionRevoker;
import com.scaffold.security.config.TokenStore;
import com.scaffold.security.vo.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityRbacUserSessionAdapter implements RbacCurrentUser, RbacSessionRevoker {

    private final TokenStore tokenStore;

    @Override
    public Long userId() {
        return LoginUser.userId();
    }

    @Override
    public String username() {
        return LoginUser.username();
    }

    @Override
    public void revokeUserSessions(Long userId) {
        if (userId != null) {
            tokenStore.del(userId.toString());
        }
    }
}
