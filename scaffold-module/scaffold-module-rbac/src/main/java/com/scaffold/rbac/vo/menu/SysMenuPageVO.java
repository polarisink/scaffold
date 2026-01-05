package com.scaffold.rbac.vo.menu;


import com.scaffold.base.util.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统功能表(SysMenu)分页请求类
 *
 * @author makejava
 * @since 2024-07-26 11:35:03
 */
@Data
@Schema(name = "SysMenu分页对象", description = "系统功能表")
public class SysMenuPageVO extends PageRequest implements Serializable {

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "菜单类型 0目录 1菜单")
    private Integer menuType;

    @Schema(description = "菜单URL")
    private String menuUrl;

    @Schema(description = "菜单图标URL")
    private String menuIconUrl;

    @Schema(description = "父菜单ID")
    private String parentId;

    @Schema(description = "排序号")
    private Integer sortNo;
}

