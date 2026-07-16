package com.scaffold.bizlog.component;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/update")
    public String updateUser(@RequestBody UserInfo user) {
        boolean result = userService.updateUser(user);
        return result ? "修改成功" : "修改失败";
    }

    @GetMapping("/delete/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        boolean result = userService.deleteUser(userId);
        return result ? "删除成功" : "删除失败";
    }
}