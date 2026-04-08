package com.scaffold.bizlog.component;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "用户管理接口", description = "用户增删改查接口")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/update")
    @Operation(summary = "更新用户", description = "传入用户信息，返回用户ID")
    @ApiResponse(responseCode = "200", description = "新增成功", content = @Content(schema = @Schema(implementation = String.class)))
    public String updateUser(@RequestBody UserInfo user) {
        boolean result = userService.updateUser(user);
        return result ? "修改成功" : "修改失败";
    }

    @GetMapping("/delete/{userId}")
    @Operation(summary = "根据ID删除用户", description = "传入用户ID，删除用户数据")
    public String deleteUser(@PathVariable("userId") Long userId) {
        boolean result = userService.deleteUser(userId);
        return result ? "删除成功" : "删除失败";
    }
}