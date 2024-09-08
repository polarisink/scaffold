package com.scaffold.biz.module.rbac.entity;


import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.scaffold.biz.rbac.module.entity.proxy.SysRoleProxy;
import com.scaffold.core.orm.vo.BaseAuditable;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 角色(SysRole)表实体类
 *
 * @author aries
 * @since 2024-07-22 20:38:40
 */
@Data
@NoArgsConstructor
@EntityProxy
@Entity
@Table
@jakarta.persistence.Table(name = "sys_role")
public class SysRole extends BaseAuditable implements Serializable, ProxyEntityAvailable<SysRole, SysRoleProxy> {

    /**
     * 角色名
     */
    private String roleName;
    /**
     * 角色编码
     */
    private String roleCode;
    /**
     * 描述
     */
    private String description;

    public SysRole(String roleName, String roleCode) {
        this.roleName = roleName;
        this.roleCode = roleCode;
    }

}

