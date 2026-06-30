package com.scaffold.rbac.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.scaffold.log.LoginLogEvent;
import com.scaffold.rbac.auth.RbacAccountService;
import com.scaffold.rbac.auth.RbacLoginUser;
import com.scaffold.rbac.vo.auth.LoginVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysAuthService {
    private final RbacAccountService accountService;

    public String login(LoginVo vo) {
        RbacLoginUser loginUser = accountService.login(vo.username(), vo.password());
        StpUtil.login(loginUser.userId());
        StpUtil.getSession().set("username", loginUser.username());
        StpUtil.getSession().set("roles", loginUser.roleCodeList());
        LoginLogEvent event = new LoginLogEvent();
        event.setUsername(vo.username());
        event.setUserId(loginUser.userId());
        event.setUsername(loginUser.username());
        SpringUtil.getApplicationContext().publishEvent(event);
        return StpUtil.getTokenValue();
    }

    public void logout() {
        StpUtil.logout();
    }
}
