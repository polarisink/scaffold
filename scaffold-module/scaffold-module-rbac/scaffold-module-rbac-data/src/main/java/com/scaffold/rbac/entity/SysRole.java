package com.scaffold.rbac.entity;


import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

/**
 * 角色(SysRole)表实体类
 *
 * @author aries
 * @since 2024-07-22 20:38:40
 */
@Data
@NoArgsConstructor
@Entity
@jakarta.persistence.Table(name = "sys_role")
public class SysRole extends BaseAuditable implements Serializable {

    /**
     * 角色名
     */
    @Column(length = 64)
    @Comment("角色名称")
    private String roleName;
    /**
     * 角色编码
     */
    @Column(length = 64)
    @Comment("角色编码")
    private String roleCode;
    /**
     * 描述
     */
    @Comment("角色描述")
    private String description;

    public SysRole(String roleName, String roleCode) {
        this.roleName = roleName;
        this.roleCode = roleCode;
    }

}
