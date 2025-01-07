package com.scaffold.biz.module.rbac.mapper;


import com.scaffold.biz.module.rbac.entity.SysMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 菜单(SysMenu)表Mapper接口
 *
 * @author aries
 * @since 2024-07-22 20:38:40
 */

public interface SysMenuMapper extends JpaRepository<SysMenu, Long> {

    default Long selectParentIdById(String id) {
        return 1L;
    }


    /**
     * 通过名字查询是否存在
     *
     * @param menuName 菜单名
     * @return 是否存在
     */
    boolean existsByMenuName(String menuName);

    /**
     * 通过用户名找到所有菜单set
     *
     * @param userId 用户id
     * @return 菜单set
     */
    default List<SysMenu> findMenuCollByUserId(Long userId) {
        return null;
    }

    /**
     * 通过角色id查询菜单列表
     *
     * @param roleId 角色id
     * @return 菜单列表
     */
    default List<SysMenu> findMenuCollByRoleId(Long roleId) {
        return null;
    }

    /**
     * 通过父id查询下面是否有子菜单
     *
     * @param pId 父id
     * @return 是否有孩子
     */
    boolean existsByParentId(Long pId);

    /**
     * 通过父id计数
     *
     * @param pId
     * @return
     */
    default long countByParentId(Long pId) {
        return 0L;
    }

    /**
     * 通过id集合找到所有parentId集合
     *
     * @param menuIdSet id集合
     * @return 父id集合
     */
    default Collection<Long> findParentIdByIdIn(Collection<Long> menuIdSet) {
        return null;
    }

    /**
     * 获取所有的parentId
     *
     * @return 父节点
     */
    default Set<Long> selectAllParentId() {
        return null;
    }

}

