package com.scaffold.rbac.controller;

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
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SaWebFluxAuthController {
    private final RbacAccountService accountService;

    @PostMapping("/login")
    public Mono<R<SaTokenInfo>> login(@RequestBody LoginVo request) {
        return Mono.fromCallable(() -> {
                    RbacLoginUser user = accountService.login(request.username(), request.password());
                    StpUtil.login(user.userId());
                    StpUtil.getSession().set("username", user.username());
                    StpUtil.getSession().set("roles", user.roleCodeList());
                    return R.success(StpUtil.getTokenInfo());
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/logout")
    public Mono<R<Void>> logout() {
        return Mono.fromCallable(() -> {
                    StpUtil.logout();
                    return R.<Void>success();
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/token-info")
    public Mono<R<SaTokenInfo>> tokenInfo() {
        return Mono.fromCallable(() -> R.success(StpUtil.getTokenInfo()))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
