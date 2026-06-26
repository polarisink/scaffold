package com.scaffold.rbac.entity;


import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.Column;
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
    @Column(name = "user_id", columnDefinition = "bigint comment '用户ID'")
    private Long userId;
    /**
     * 角色id
     */
    @Column(name = "role_id", columnDefinition = "bigint comment '角色ID'")
    private Long roleId;


    public SysUserRole(Long userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }
}
