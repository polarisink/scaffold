package com.scaffold.support.identity;

import com.scaffold.rbac.auth.RbacCurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 从 Spring Security 认证上下文中解析可信的售后用户身份。
 */
@Component
@RequiredArgsConstructor
public class SecuritySupportCurrentUserProvider implements SupportCurrentUserProvider {
    private final RbacCurrentUser rbacCurrentUser;

    @Override
    public long requireUserId() {

        Long userId = rbacCurrentUser.userId();
        if (userId == null) {
            throw new IllegalStateException("authenticated user is required");
        }
        return userId;
    }
}
