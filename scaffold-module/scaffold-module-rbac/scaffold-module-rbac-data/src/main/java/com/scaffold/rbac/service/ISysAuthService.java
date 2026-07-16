package com.scaffold.rbac.service;

import com.mzt.logapi.starter.annotation.LogRecord;
import com.scaffold.rbac.vo.auth.LoginVo;

import static com.scaffold.rbac.contant.RbacLogConst.AUTH;
import static com.scaffold.rbac.contant.RbacLogConst.LOGIN;
import static com.scaffold.rbac.contant.RbacLogConst.LOGOUT;

/**
 * 鉴权接口
 */
public interface ISysAuthService {

    /**
     * 登录
     *
     * @param vo 登录请求
     * @return token
     */
    @LogRecord(
            type = AUTH,
            subType = LOGIN,
            success = "用户【{{#vo.username}}】登录成功",
            fail = "用户【{{#vo.username}}】登录失败，原因：{{#_errorMsg}}",
            bizNo = "{{#userId}}",
            operator = "{{#vo.username}}"
    )
    String login(LoginVo vo);

    /**
     * 登出
     */
    @LogRecord(
            type = AUTH,
            subType = LOGOUT,
            success = "用户【{{#username}}】退出成功",
            fail = "用户【{{#username}}】退出失败，原因：{{#_errorMsg}}",
            bizNo = "{{#userId}}",
            operator = "{{#username}}"
    )
    void logout();
}
