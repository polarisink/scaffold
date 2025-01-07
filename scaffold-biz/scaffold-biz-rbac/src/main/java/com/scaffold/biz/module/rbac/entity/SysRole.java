package com.scaffold.biz.module.rbac.entity;


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
@Entity
@jakarta.persistence.Table(name = "sys_role")
public class SysRole extends BaseAuditable implements Serializable {

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

