package com.scaffold.rbac.auth.sa.webflux;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.scaffold.base.util.R;
import com.scaffold.rbac.auth.RbacAccountService;
import com.scaffold.rbac.auth.RbacLoginUser;
import com.scaffold.rbac.vo.auth.LoginVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SaWebFluxAuthController {
    private final RbacAccountService accountService;

    @PostMapping("/login")
    public R<SaTokenInfo> login(@RequestBody LoginVo request) {
        RbacLoginUser user = accountService.login(request.username(), request.password());
        StpUtil.login(user.userId());
        StpUtil.getSession().set("username", user.username());
        StpUtil.getSession().set("roles", user.roleCodeList());
        return R.success(StpUtil.getTokenInfo());
    }

    @PostMapping("/logout")
    public R<Void> logout() {
        StpUtil.logout();
        return R.success();
    }

    @GetMapping("/token-info")
    public R<SaTokenInfo> tokenInfo() {
        return R.success(StpUtil.getTokenInfo());
    }
}
