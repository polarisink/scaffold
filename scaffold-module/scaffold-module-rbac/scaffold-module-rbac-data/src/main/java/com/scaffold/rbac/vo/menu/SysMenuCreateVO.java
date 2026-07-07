package com.scaffold.rbac.vo.menu;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统功能表(SysMenu)创建请求类
 *
 * @author makejava
 * @since 2024-07-26 11:35:03
 */
@Data
@Schema(name = "SysMenu新增对象", description = "系统功能表")
public class SysMenuCreateVO implements Serializable {

    @NotBlank(message = "菜单名称不可为空！")
    @Schema(description = "菜单名称")
    private String menuName;

    @NotNull(message = "菜单类型不可为空!")
    @Schema(description = "菜单类型 0目录 1菜单")
    private Integer menuType;

    @NotBlank(message = "路径不可为空！")
    @Schema(description = "路径")
    private String path;

    @Schema(description = "菜单URL")
    private String menuUrl;

    @Schema(description = "菜单图标URL")
    private String menuIconUrl;

    @NotNull(message = "父才菜单id不可为空！")
    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "排序号")
    private Integer sortNo;

}

