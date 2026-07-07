package com.scaffold.rbac.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.scaffold.base.util.R;
import com.scaffold.rbac.auth.RbacAccountService;
import com.scaffold.rbac.auth.RbacLoginUser;
import com.scaffold.rbac.service.RbacLogRecordService;
import com.scaffold.rbac.vo.auth.LoginVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SaWebFluxAuthController {
    private final RbacAccountService accountService;
    private final RbacLogRecordService logRecordService;

    @PostMapping("/login")
    public Mono<R<SaTokenInfo>> login(@RequestBody LoginVo request, ServerWebExchange exchange) {
        return Mono.fromCallable(() -> {
                    try {
                        RbacLoginUser user = accountService.login(request.username(), request.password());
                        StpUtil.login(user.userId());
                        StpUtil.getSession().set("username", user.username());
                        StpUtil.getSession().set("roles", user.roleCodeList());
                        logRecordService.recordLogin(user.userId(), user.username(),
                                RbacLogRecordService.ACTION_LOGIN, true, "登录成功",
                                clientIp(exchange), userAgent(exchange));
                        return R.success(StpUtil.getTokenInfo());
                    } catch (RuntimeException exception) {
                        logRecordService.recordLogin(null, request.username(),
                                RbacLogRecordService.ACTION_LOGIN, false, exception.getMessage(),
                                clientIp(exchange), userAgent(exchange));
                        throw exception;
                    }
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/logout")
    public Mono<R<Void>> logout(ServerWebExchange exchange) {
        return Mono.fromCallable(() -> {
                    Long userId = StpUtil.isLogin() ? StpUtil.getLoginIdAsLong() : null;
                    Object usernameValue = userId == null ? null : StpUtil.getSession().get("username");
                    StpUtil.logout();
                    if (userId != null) {
                        logRecordService.recordLogin(userId,
                                usernameValue == null ? userId.toString() : usernameValue.toString(),
                                RbacLogRecordService.ACTION_LOGOUT, true, "退出成功",
                                clientIp(exchange), userAgent(exchange));
                    }
                    return R.<Void>success();
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/token-info")
    public Mono<R<SaTokenInfo>> tokenInfo() {
        return Mono.fromCallable(() -> R.success(StpUtil.getTokenInfo()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private static String clientIp(ServerWebExchange exchange) {
        return exchange.getRequest().getRemoteAddress() == null ? null
                : exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
    }

    private static String userAgent(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst(HttpHeaders.USER_AGENT);
    }
}
