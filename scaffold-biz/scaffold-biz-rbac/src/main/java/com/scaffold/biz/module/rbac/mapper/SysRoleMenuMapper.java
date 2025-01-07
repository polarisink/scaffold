package com.scaffold.biz.module.rbac.mapper;


import com.scaffold.biz.module.rbac.entity.SysRoleMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * 角色菜单表(SysRoleMenu)表Mapper接口
 *
 * @author aries
 * @since 2024-07-22 20:38:41
 */
public interface SysRoleMenuMapper extends JpaRepository<SysRoleMenu, Long> {
    /**
     * 通过角色id和菜单id删除
     *
     * @param roleId     角色id
     * @param menuIdColl 菜单id集合，可为空,为空删除该角色下所有菜单的关联关系
     */
    void deleteByRoleIdAndMenuIdIn(@NotNull Long roleId, @Nullable Collection<Long> menuIdColl);

    /**
     * 通过菜单id查询角色id集合
     *
     * @param menuId 菜单id
     * @return 角色id集合
     */
    default List<Long> selectRoleIdCollByMenuId(Long menuId) {
        return null;
    }

    /**
     * 通过角色id查到所有菜单id
     *
     * @param roleId 角色id
     * @return 菜单id集合
     */
    default List<Long> selectMenuIdByRoleId(Long roleId) {
        return null;
    }
}

