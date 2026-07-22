package com.scaffold.rbac.controller;

import com.scaffold.rbac.service.ISysAuthService;
import com.scaffold.rbac.vo.auth.LoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "鉴权", description = "鉴权")
public class SysAuthController {
    private final ISysAuthService authService;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public String login(@RequestBody LoginVo loginVO) {
        return authService.login(loginVO);
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public void logout() {
        authService.logout();
    }
}
