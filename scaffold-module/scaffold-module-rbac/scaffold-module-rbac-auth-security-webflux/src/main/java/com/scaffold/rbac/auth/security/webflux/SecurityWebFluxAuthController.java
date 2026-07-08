package com.scaffold.rbac.auth.security.webflux;

import com.scaffold.base.util.R;
import com.scaffold.rbac.auth.RbacAccountService;
import com.scaffold.rbac.auth.RbacLoginUser;
import com.scaffold.rbac.service.RbacLogRecordService;
import com.scaffold.rbac.vo.auth.LoginVo;
import com.scaffold.security.config.TokenStore;
import com.scaffold.security.util.JwtUtil;
import com.scaffold.security.vo.PayloadDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SecurityWebFluxAuthController {
    private final RbacAccountService accountService;
    private final TokenStore tokenStore;
    private final JwtUtil jwtUtil;
    private final RbacLogRecordService logRecordService;

    @PostMapping("/login")
    public Mono<R<SecurityWebFluxTokenInfo>> login(@RequestBody LoginVo request, ServerWebExchange exchange) {
        return Mono.fromCallable(() -> {
                    try {
                        RbacLoginUser user = accountService.login(request.username(), request.password());
                        String token = jwtUtil.generateToken(
                                PayloadDTO.of(user.userId(), user.username(), user.roleCodeList()));
                        tokenStore.set(user.userId().toString(), token);
                        logRecordService.recordLogin(user.userId(), user.username(),
                                RbacLogRecordService.ACTION_LOGIN, true, "登录成功",
                                clientIp(exchange), userAgent(exchange));
                        return R.success(new SecurityWebFluxTokenInfo(
                                HttpHeaders.AUTHORIZATION, token, user.userId(), user.username()));
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
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(context -> {
                    Long userId = Long.valueOf(context.getAuthentication().getPrincipal().toString());
                    String username = context.getAuthentication().getCredentials().toString();
                    return Mono.fromRunnable(() -> {
                                tokenStore.del(userId.toString());
                                logRecordService.recordLogin(userId, username,
                                        RbacLogRecordService.ACTION_LOGOUT, true, "退出成功",
                                        clientIp(exchange), userAgent(exchange));
                            })
                            .subscribeOn(Schedulers.boundedElastic());
                })
                .thenReturn(R.success());
    }

    @GetMapping("/token-info")
    public Mono<R<SecurityWebFluxTokenInfo>> tokenInfo() {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(context -> Mono.fromCallable(() -> {
                    Long userId = Long.valueOf(context.getAuthentication().getPrincipal().toString());
                    String username = context.getAuthentication().getCredentials().toString();
                    String token = tokenStore.get(userId.toString());
                    return R.success(new SecurityWebFluxTokenInfo(HttpHeaders.AUTHORIZATION, token, userId, username));
                }).subscribeOn(Schedulers.boundedElastic()));
    }

    private static String clientIp(ServerWebExchange exchange) {
        return exchange.getRequest().getRemoteAddress() == null ? null
                : exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
    }

    private static String userAgent(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst(HttpHeaders.USER_AGENT);
    }
}
