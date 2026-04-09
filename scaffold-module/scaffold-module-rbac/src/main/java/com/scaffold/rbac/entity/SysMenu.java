package com.scaffold.rbac.entity;


import com.scaffold.base.util.ITree;
import com.scaffold.orm.BaseAuditable;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Column(name = "parent_id", columnDefinition = "bigint comment '父菜单ID'")
    private Long parentId;
    /**
     * 菜单名称
     */
    @Column(name = "menu_name", columnDefinition = "varchar(64) comment '菜单名称'")
    private String menuName;

    @Schema(description = "路径")
    @Column(name = "path", columnDefinition = "varchar(255) comment '路由路径'")
    private String path;
    /**
     * 菜单类型
     */
    @Column(name = "menu_type", columnDefinition = "int comment '菜单类型'")
    private Integer menuType;
    /**
     * 菜单URL
     */
    @Column(name = "menu_url", columnDefinition = "varchar(255) comment '菜单URL'")
    private String menuUrl;
    /**
     * 菜单图标URL
     */
    @Column(name = "menu_icon_url", columnDefinition = "varchar(255) comment '菜单图标URL'")
    private String menuIconUrl;
    /**
     * 排序号
     */
    @Column(name = "sort_no", columnDefinition = "int comment '排序号'")
    private Integer sortNo;

    @Transient
    private List<SysMenu> children;

}
