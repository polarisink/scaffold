package com.scaffold.biz.module.rbac.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

/**
 * 密码更新请求
 *
 * @param oldPasswd 旧密码
 * @param newPasswd 新密码
 */
public record PasswdUpdateVO(
        @NotEmpty(message = "旧密码不能为空") @Schema(description = "旧密码") String oldPasswd,
        @NotEmpty(message = "新密码不能为空") @Schema(description = "新密码") String newPasswd) {
}
