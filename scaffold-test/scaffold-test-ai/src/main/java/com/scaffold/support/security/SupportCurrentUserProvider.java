package com.scaffold.support.security;

/** Boundary for obtaining the authenticated user without accepting identity from model or request parameters. */
public interface SupportCurrentUserProvider {

    long requireUserId();
}
