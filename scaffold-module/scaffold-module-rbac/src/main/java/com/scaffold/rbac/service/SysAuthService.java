package com.scaffold.rbac.service;

import com.scaffold.rbac.vo.auth.LoginVO;

public interface SysAuthService {
    String login(LoginVO loginVO);

    void logout();
}
