package com.scaffold.rbac.entity;


import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

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
    @Comment("角色ID")
    private Long roleId;
    /**
     * 菜单id
     */
    @Comment("菜单ID")
    private Long menuId;

    public SysRoleMenu(Long roleId, Long menuId) {
        this.roleId = roleId;
        this.menuId = menuId;
    }


}
