package com.scaffold.rbac.service;

import cn.dev33.satoken.stp.StpUtil;
import com.mzt.logapi.context.LogRecordContext;
import com.scaffold.rbac.auth.RbacAccountService;
import com.scaffold.rbac.auth.RbacLoginUser;
import com.scaffold.rbac.components.SaRbacCurrentUser;
import com.scaffold.rbac.vo.auth.LoginVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysAuthService implements ISysAuthService {
    private final RbacAccountService accountService;

    @Override
    public String login(LoginVo vo) {
        RbacLoginUser loginUser = accountService.login(vo.username(), vo.password());
        StpUtil.login(loginUser.userId());
        StpUtil.getSession().set("username", loginUser.username());
        StpUtil.getSession().set("roles", loginUser.roleCodeList());
        LogRecordContext.putVariable("userId", loginUser.userId());
        return StpUtil.getTokenValue();
    }

    @Override
    public void logout() {
        Long userId = SaRbacCurrentUser.userId();
        String username = SaRbacCurrentUser.username();
        if (userId != null) {
            LogRecordContext.putVariable("userId", userId);
            LogRecordContext.putVariable("username", username);
        }
        StpUtil.logout();
    }
}
