package com.scaffold.rbac.controller;

import com.scaffold.rbac.service.SysAuthService;
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
    private final SysAuthService authService;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public String login(@RequestBody LoginVo loginVO) {
        return authService.login(loginVO);
    }

    @Operation(summary = "登出")
    @GetMapping("/logout")
    public void logout() {
        authService.logout();
    }
}
