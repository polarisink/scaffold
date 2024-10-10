package com.scaffold.biz.module.rbac.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scaffold.core.orm.vo.BaseAuditable;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "名字")
    private String username;

    @Schema(description = "封禁状态:0，未封禁；1：封禁。默认0")
    private Boolean status = true;

    @JsonIgnore
    private String password;

    @Schema(description = "组织id")
    private String orgId;


    /*@Schema(description = "角色集合")
    @Transient
    @Navigate(value = RelationTypeEnum.OneToMany)
    private List<RoleVO> roleList = new ArrayList<>();*/
}

