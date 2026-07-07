package com.scaffold.rbac.service;

import com.scaffold.rbac.vo.auth.LoginVo;

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
    String login(LoginVo vo);

    /**
     * 登出
     */
    void logout();
}