package com.scaffold.rbac.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;

import java.io.Serializable;

/**
 * (SysUser)表实体类
 *
 * @author aries
 * @since 2024-07-22 20:40:07
 */
@Data
@Entity
@jakarta.persistence.Table(name = "sys_user")
public class SysUser extends BaseAuditable implements Serializable {

    @Column(columnDefinition = "varchar(64) comment '用户名'")
    private String username;

    @Column(columnDefinition = "tinyint(1) default 1 comment '状态:0禁用;1启用'")
    private Boolean status = true;

    @JsonIgnore
    @Column(columnDefinition = "varchar(255) comment '密码'")
    private String password;

    @Column(columnDefinition = "bigint comment '组织ID'")
    private Long orgId;
}
