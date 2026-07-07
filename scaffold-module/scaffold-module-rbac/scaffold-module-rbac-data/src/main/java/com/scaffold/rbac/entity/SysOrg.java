package com.scaffold.rbac.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.scaffold.base.util.ITree;
import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

import java.util.List;

/**
 * 部门表
 */
@Data
@Table(name = "sys_org")
@Entity
@TableName("sys_org")
public class SysOrg extends BaseAuditable implements ITree<SysOrg, Long> {
    @Column(columnDefinition = "bigint not null comment  '上级部门id'")
    private Long parentId;
    @Column(columnDefinition = "varchar(50) not null comment '部门名字'")
    private String orgName;
    @Column(columnDefinition = "int comment '部门排序（同级生效）'")
    private Integer sort;
    @Column(columnDefinition = "varchar(50) comment '部门编码'")
    private String orgCode;
    @Transient
    @TableField(exist = false)
    private List<SysOrg> children;
}
