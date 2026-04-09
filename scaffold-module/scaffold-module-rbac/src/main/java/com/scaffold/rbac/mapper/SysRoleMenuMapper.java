package com.scaffold.rbac.mapper;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scaffold.orm.MyBaseMapper;
import com.scaffold.rbac.entity.SysRoleMenu;

import java.util.Collection;
import java.util.List;

/**
 * 角色菜单表(SysRoleMenu)表Mapper接口
 *
 * @author aries
 * @since 2024-07-22 20:38:41
 */
public interface SysRoleMenuMapper extends MyBaseMapper<SysRoleMenu> {
    /**
     * 通过角色id和菜单id删除
     *
     * @param roleId     角色id
     * @param menuIdColl 菜单id集合，可为空,为空删除该角色下所有菜单的关联关系
     */
    default void deleteByRoleIdAndMenuIdIn(Long roleId, Collection<Long> menuIdColl) {
        delete(Wrappers.<SysRoleMenu>lambdaQuery()
                .eq(SysRoleMenu::getRoleId, roleId)
                .in(menuIdColl != null && !menuIdColl.isEmpty(), SysRoleMenu::getMenuId, menuIdColl));
    }

    /**
     * 通过菜单id查询角色id集合
     *
     * @param menuId 菜单id
     * @return 角色id集合
     */
    default List<Long> selectRoleIdCollByMenuId(Long menuId) {
        return selectList(Wrappers.<SysRoleMenu>lambdaQuery()
                .eq(SysRoleMenu::getMenuId, menuId)
                .select(SysRoleMenu::getRoleId))
                .stream()
                .map(SysRoleMenu::getRoleId)
                .distinct()
                .toList();
    }

    /**
     * 通过角色id查到所有菜单id
     *
     * @param roleId 角色id
     * @return 菜单id集合
     */
    default List<Long> selectMenuIdByRoleId(Long roleId) {
        return selectList(Wrappers.<SysRoleMenu>lambdaQuery()
                .eq(SysRoleMenu::getRoleId, roleId)
                .select(SysRoleMenu::getMenuId))
                .stream()
                .map(SysRoleMenu::getMenuId)
                .sorted()
                .toList();
    }
}
