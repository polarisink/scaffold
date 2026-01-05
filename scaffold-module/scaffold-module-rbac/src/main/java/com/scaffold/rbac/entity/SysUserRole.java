package com.scaffold.rbac.entity;


import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户角色表(SysUserRole)表实体类
 *
 * @author aries
 * @since 2024-07-22 20:38:41
 */
@Data
@NoArgsConstructor
@Entity
@jakarta.persistence.Table(name = "sys_user_role")
public class SysUserRole extends BaseAuditable implements Serializable {

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 角色id
     */
    private Long roleId;


    public SysUserRole(Long userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }
}

