package com.scaffold.rbac.service;

import cn.dev33.satoken.stp.StpUtil;
import com.scaffold.rbac.components.SaRbacCurrentUser;
import com.scaffold.rbac.auth.RbacAccountService;
import com.scaffold.rbac.auth.RbacLoginUser;
import com.scaffold.rbac.vo.auth.LoginVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysAuthService implements ISysAuthService{
    private final RbacAccountService accountService;
    private final RbacLogRecordService logRecordService;

    @Override
    public String login(LoginVo vo) {
        try {
            RbacLoginUser loginUser = accountService.login(vo.username(), vo.password());
            StpUtil.login(loginUser.userId());
            StpUtil.getSession().set("username", loginUser.username());
            StpUtil.getSession().set("roles", loginUser.roleCodeList());
            logRecordService.recordLogin(loginUser.userId(), loginUser.username(),
                    RbacLogRecordService.ACTION_LOGIN, true, "登录成功", null, null);
            return StpUtil.getTokenValue();
        } catch (RuntimeException exception) {
            logRecordService.recordLogin(null, vo.username(), RbacLogRecordService.ACTION_LOGIN,
                    false, exception.getMessage(), null, null);
            throw exception;
        }
    }

    @Override
    public void logout() {
        Long userId = SaRbacCurrentUser.userId();
        String username = SaRbacCurrentUser.username();
        StpUtil.logout();
        if (userId != null) {
            logRecordService.recordLogin(userId, username, RbacLogRecordService.ACTION_LOGOUT,
                    true, "退出成功", null, null);
        }
    }
}
