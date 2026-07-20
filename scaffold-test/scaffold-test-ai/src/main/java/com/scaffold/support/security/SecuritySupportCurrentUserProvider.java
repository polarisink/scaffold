package com.scaffold.support.security;

import com.scaffold.rbac.auth.RbacCurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Resolves the trusted support user identity established by Spring Security. */
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
