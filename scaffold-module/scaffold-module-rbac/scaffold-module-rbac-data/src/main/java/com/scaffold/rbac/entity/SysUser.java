package com.scaffold.rbac.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scaffold.orm.BaseLongAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import org.hibernate.annotations.Comment;

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
public class SysUser extends BaseLongAuditable implements Serializable {

    @Column(length = 64)
    @Comment("用户名")
    private String username;

    @Comment("状态:0禁用;1启用")
    private Boolean status = true;

    @JsonIgnore
    @Comment("密码")
    private String password;

    @Comment("组织ID")
    private Long orgId;
}
