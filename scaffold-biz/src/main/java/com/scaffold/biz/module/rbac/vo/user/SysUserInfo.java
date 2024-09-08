package com.scaffold.biz.module.rbac.vo.user;

import com.scaffold.biz.module.rbac.entity.SysMenu;
import com.scaffold.biz.module.rbac.entity.SysUser;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "用户信息")
public record SysUserInfo(
        @Schema(description = "用户")
        SysUser user,
        @Schema(description = "菜单")
        List<SysMenu> menus) {
}
