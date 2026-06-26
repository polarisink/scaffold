package com.scaffold.rbac.components;

import cn.dev33.satoken.stp.StpUtil;

public final class SaRbacCurrentUser {

    private SaRbacCurrentUser() {
    }

    public static Long userId() {
        return StpUtil.isLogin() ? StpUtil.getLoginIdAsLong() : null;
    }

    public static String username() {
        if (!StpUtil.isLogin()) {
            return null;
        }
        Object username = StpUtil.getSession().get("username");
        return username == null ? null : username.toString();
    }
}
