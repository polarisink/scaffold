package com.scaffold.rbac.vo.menu;


import com.scaffold.base.util.ITree;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统功能表(SysMenu)结果类
 *
 * @author makejava
 * @since 2024-07-26 11:35:03
 */
@Data
@Schema(name = "SysMenu结果对象", description = "系统功能表")
public class SysMenuResultVO implements Serializable, ITree<SysMenuResultVO, String> {


    @Schema(description = "主键")
    private String id;

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

    @Schema(description = "下级组织")
    private List<SysMenuResultVO> children = new ArrayList<>();
}

