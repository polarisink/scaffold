package com.scaffold.rbac.mapper;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scaffold.orm.MyBaseMapper;
import com.scaffold.rbac.entity.SysMenu;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 菜单(SysMenu)表Mapper接口
 *
 * @author aries
 * @since 2024-07-22 20:38:40
 */
public interface SysMenuMapper extends MyBaseMapper<SysMenu> {


    /**
     * 通过名字查询是否存在
     *
     * @param menuName 菜单名
     * @return 是否存在
     */
    default boolean existsByMenuName(String menuName) {
        return exists(Wrappers.<SysMenu>lambdaQuery().eq(SysMenu::getMenuName, menuName));
    }

    /**
     * 通过用户名找到所有菜单set
     *
     * @param userId 用户id
     * @return 菜单set
     */
    default List<SysMenu> findMenuCollByUserId(Long userId) {
        return List.of();
    }

    /**
     * 通过角色id查询菜单列表
     *
     * @param roleId 角色id
     * @return 菜单列表
     */
    default List<SysMenu> findMenuCollByRoleId(Long roleId) {
        return List.of();
    }

    /**
     * 通过父id查询下面是否有子菜单
     *
     * @param pId 父id
     * @return 是否有孩子
     */
    default boolean existsByParentId(Long pId) {
        return selectCount(Wrappers.<SysMenu>lambdaQuery().eq(SysMenu::getParentId, pId)) > 0;
    }

    /**
     * 通过父id计数
     *
     * @param pId
     * @return
     */
    default long countByParentId(Long pId) {
        return selectCount(Wrappers.<SysMenu>lambdaQuery().eq(SysMenu::getParentId, pId));
    }

    /**
     * 通过id集合找到所有parentId集合
     *
     * @param menuIdSet id集合
     * @return 父id集合
     */
    default Collection<Long> findParentIdByIdIn(Collection<Long> menuIdSet) {
        if (menuIdSet == null || menuIdSet.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapper<SysMenu> wrapper = Wrappers.<SysMenu>lambdaQuery()
                .in(SysMenu::getId, menuIdSet)
                .select(SysMenu::getParentId);
        return selectList(wrapper).stream().map(SysMenu::getParentId).collect(Collectors.toSet());
    }

    /**
     * 获取所有的parentId
     *
     * @return 父节点
     */
    default Set<Long> selectAllParentId() {
        return selectList(Wrappers.<SysMenu>lambdaQuery().select(SysMenu::getParentId))
                .stream()
                .map(SysMenu::getParentId)
                .collect(Collectors.toSet());
    }

}
