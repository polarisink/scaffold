package com.scaffold.rbac.components;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.exception.SaTokenContextException;

public final class SaRbacCurrentUser {

    private SaRbacCurrentUser() {
    }

    public static Long userId() {
        try {
            return StpUtil.isLogin() ? StpUtil.getLoginIdAsLong() : null;
        } catch (SaTokenContextException ignored) {
            return null;
        }
    }

    public static String username() {
        try {
            if (!StpUtil.isLogin()) {
                return null;
            }
            Object username = StpUtil.getSession().get("username");
            return username == null ? null : username.toString();
        } catch (SaTokenContextException ignored) {
            return null;
        }
    }
}
