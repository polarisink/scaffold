package com.scaffold.rbac.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.scaffold.base.util.ITree;
import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 系统功能表(SysMenu)表实体类
 *
 * @author makejava
 * @since 2024-07-26 11:42:26
 */
@Data
@Entity
@jakarta.persistence.Table(name = "sys_menu")
public class SysMenu extends BaseAuditable implements Serializable, ITree<SysMenu, Long> {

    /**
     * 父菜单ID
     */
    @Column(columnDefinition = "bigint comment '父菜单ID'")
    private Long parentId;
    /**
     * 菜单名称
     */
    @Column(columnDefinition = "varchar(64) comment '菜单名称'")
    private String menuName;

    @Column(columnDefinition = "varchar(255) comment '路由路径'")
    private String path;
    /**
     * 菜单类型
     */
    @Column(columnDefinition = "int comment '菜单类型'")
    private Integer menuType;
    /**
     * 菜单URL
     */
    @Column(columnDefinition = "varchar(255) comment '菜单URL'")
    private String menuUrl;
    /**
     * 菜单图标URL
     */
    @Column(columnDefinition = "varchar(255) comment '菜单图标URL'")
    private String menuIconUrl;
    /**
     * 排序号
     */
    @Column(columnDefinition = "int comment '排序号'")
    private Integer sortNo;

    @Transient
    @TableField(exist = false)
    private List<SysMenu> children;

}
