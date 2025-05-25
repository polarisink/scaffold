package com.scaffold.biz.module.rbac.entity;


import com.scaffold.core.orm.vo.BaseAuditable;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 角色菜单表(SysRoleMenu)表实体类
 *
 * @author aries
 * @since 2024-07-22 20:38:41
 */
@Data
@NoArgsConstructor
@Entity
@jakarta.persistence.Table(name = "sys_role_menu")
public class SysRoleMenu extends BaseAuditable implements Serializable {

    /**
     * 角色id
     */
    private Long roleId;
    /**
     * 菜单id
     */
    private Long menuId;

    public SysRoleMenu(Long roleId, Long menuId) {
        this.roleId = roleId;
        this.menuId = menuId;
    }


}

