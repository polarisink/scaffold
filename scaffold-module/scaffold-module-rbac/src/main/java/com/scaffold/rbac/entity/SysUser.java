package com.scaffold.rbac.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scaffold.orm.BaseAuditable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    @Column(name = "username", columnDefinition = "varchar(64) comment '用户名'")
    private String username;

    @Schema(description = "封禁状态:0，未封禁；1：封禁。默认0")
    @Column(name = "status", columnDefinition = "tinyint(1) default 1 comment '状态:0禁用;1启用'")
    private Boolean status = true;

    @JsonIgnore
    @Column(name = "password", columnDefinition = "varchar(255) comment '密码'")
    private String password;

    @Schema(description = "组织id")
    @Column(name = "org_id", columnDefinition = "varchar(64) comment '组织ID'")
    private String orgId;

    public void x() {
        List<SysUser> list = new ArrayList<>();
        Map<String, SysUser> collect = list.stream().collect(Collectors.toMap(SysUser::getUsername, Function.identity(), (e1, e2) -> e1));
    }


    /*@Schema(description = "角色集合")
    @Transient
    @Navigate(value = RelationTypeEnum.OneToMany)
    private List<RoleVO> roleList = new ArrayList<>();*/
}
