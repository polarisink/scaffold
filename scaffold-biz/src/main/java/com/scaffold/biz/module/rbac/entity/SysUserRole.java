package com.scaffold.biz.module.rbac.entity;


import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.scaffold.biz.rbac.module.entity.proxy.SysUserRoleProxy;
import com.scaffold.core.orm.vo.BaseAuditable;
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
@EntityProxy
@NoArgsConstructor
@Table
@Entity
@jakarta.persistence.Table(name = "sys_user_role")
public class SysUserRole extends BaseAuditable implements Serializable, ProxyEntityAvailable<SysUserRole, SysUserRoleProxy> {

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

