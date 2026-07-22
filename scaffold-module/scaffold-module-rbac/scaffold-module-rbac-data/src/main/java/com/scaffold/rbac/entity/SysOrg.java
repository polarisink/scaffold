package com.scaffold.rbac.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.scaffold.base.util.ITree;
import com.scaffold.orm.BaseLongAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.util.List;

/**
 * 部门表
 */
@Data
@Table(name = "sys_org")
@Entity
@TableName("sys_org")
public class SysOrg extends BaseLongAuditable implements ITree<SysOrg, Long> {
    @Column(nullable = false)
    @Comment("上级部门id")
    private Long parentId;
    @Column(nullable = false, length = 50)
    @Comment("部门名字")
    private String orgName;
    @Comment("部门排序（同级生效）")
    private Integer sort;
    @Column(length = 50)
    @Comment("部门编码")
    private String orgCode;
    @Transient
    @TableField(exist = false)
    private List<SysOrg> children;
}
