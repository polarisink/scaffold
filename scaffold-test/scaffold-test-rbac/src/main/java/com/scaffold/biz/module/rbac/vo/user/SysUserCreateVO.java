package com.scaffold.biz.module.rbac.vo.user;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * (SysUser)创建请求类
 *
 * @param username   用户名
 * @param password   密码
 * @param orgId      组织id
 * @param roleIdList 角色id集合
 * @author aries
 * @since 2024-07-22 20:40:08
 */
@Schema(name = "SysUser新增对象")
public record SysUserCreateVO(

        @NotBlank(message = "用户名不能为空") @Schema(description = "名字") String username,
        @NotBlank(message = "密码不能为空") @Schema(description = "密码") String password,
        @NotBlank(message = "组织不能为空") @Schema(description = "组织id") String orgId,
        @Schema(description = "岗位id") String positionId,
        @NotEmpty(message = "用户必须包含角色") @Schema(description = "角色id集合") List<Long> roleIdList) {
}

