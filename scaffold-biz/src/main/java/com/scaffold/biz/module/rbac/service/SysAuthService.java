package com.scaffold.biz.module.rbac.service;

import com.scaffold.biz.module.rbac.vo.auth.LoginVO;

public interface SysAuthService {
    String login(LoginVO loginVO);

    void logout();
}
