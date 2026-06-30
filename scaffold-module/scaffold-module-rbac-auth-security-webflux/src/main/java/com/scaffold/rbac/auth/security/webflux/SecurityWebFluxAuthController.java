package com.scaffold.rbac.auth.security.webflux;

import com.scaffold.base.util.R;
import com.scaffold.rbac.auth.RbacAccountService;
import com.scaffold.rbac.auth.RbacLoginUser;
import com.scaffold.rbac.vo.auth.LoginVo;
import com.scaffold.security.config.TokenService;
import com.scaffold.security.util.JwtUtil;
import com.scaffold.security.vo.PayloadDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SecurityWebFluxAuthController {
    private final RbacAccountService accountService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public R<SecurityWebFluxTokenInfo> login(@RequestBody LoginVo request) {
        RbacLoginUser user = accountService.login(request.username(), request.password());
        String token = JwtUtil.generateToken(PayloadDTO.of(user.userId(), user.username(), user.roleCodeList()));
        tokenService.set(user.userId().toString(), token);
        return R.success(new SecurityWebFluxTokenInfo(HttpHeaders.AUTHORIZATION, token, user.userId(), user.username()));
    }

    @PostMapping("/logout")
    public Mono<R<Void>> logout() {
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> Long.valueOf(context.getAuthentication().getPrincipal().toString()))
                .doOnNext(userId -> tokenService.del(userId.toString()))
                .thenReturn(R.success());
    }

    @GetMapping("/token-info")
    public Mono<R<SecurityWebFluxTokenInfo>> tokenInfo() {
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> {
                    Long userId = Long.valueOf(context.getAuthentication().getPrincipal().toString());
                    String username = context.getAuthentication().getCredentials().toString();
                    String token = tokenService.get(userId.toString());
                    return R.success(new SecurityWebFluxTokenInfo(HttpHeaders.AUTHORIZATION, token, userId, username));
                });
    }
}
