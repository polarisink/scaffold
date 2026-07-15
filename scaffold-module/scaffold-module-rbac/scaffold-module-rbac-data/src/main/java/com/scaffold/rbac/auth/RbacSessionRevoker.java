package com.scaffold.rbac.auth;

/** Revokes every active session owned by one RBAC user. */
public interface RbacSessionRevoker {

    void revokeUserSessions(Long userId);
}
