package com.scaffold.rbac.auth;

/** Supplies the current RBAC identity without exposing an authentication framework. */
public interface RbacCurrentUser {

    Long userId();

    String username();
}
