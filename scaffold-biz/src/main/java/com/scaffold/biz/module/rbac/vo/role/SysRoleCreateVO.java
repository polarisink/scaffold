package com.scaffold.biz.module.rbac.vo.role;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

/**
 * 角色(SysRole)创建请求类
 *
 * @param roleName    角色名
 * @param roleCode    角色编码
 * @param description 描述
 * @param menuIdList  菜单集合
 * @author aries
 * @since 2024-07-22 20:38:41
 */
@Schema(name = "SysRole新增对象", description = "角色")
public record SysRoleCreateVO(

        @Schema(description = "角色名") @NotBlank(message = "角色名不能为空") String roleName,

        @Schema(description = "角色编码") String roleCode,

        @Schema(description = "描述") String description,

        @Schema(description = "菜单id集合") Set<Long> menuIdList) {
}

