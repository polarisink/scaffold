package com.scaffold.rbac.vo.menu;

import com.scaffold.rbac.entity.SysMenu;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 角色菜单返回包装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "角色菜单返回包装")
public class SysRoleWrapper {
    @Schema(description = "菜单树")
    private List<SysMenu> tree;
    @Schema(description = "菜单id集合")
    private List<Long> list;

}
