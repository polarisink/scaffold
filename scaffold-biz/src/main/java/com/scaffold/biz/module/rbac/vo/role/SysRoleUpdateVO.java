package com.scaffold.biz.module.rbac.vo.role;


import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;

/**
 * 角色(SysRole)更新请求类
 *
 * @author aries
 * @since 2024-07-22 20:38:41
 */
@Schema(name = "SysRole更新对象", description = "角色")
public record SysRoleUpdateVO(@Schema(description = "id") Long id, @Schema(description = "角色名") String roleName,
                              @Schema(description = "角色编码") String roleCode,
                              @Schema(description = "描述") String description,
                              @Schema(description = "菜单id集合") List<Long> menuIdList) implements Serializable {


}

