package com.scaffold.rbac.mapper;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scaffold.orm.MyBaseMapper;
import com.scaffold.rbac.entity.SysMenu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

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

    @Select("""
            SELECT DISTINCT
                m.id,
                m.gmt_modified,
                m.gmt_created,
                m.created_by,
                m.modified_by,
                m.deleted,
                m.parent_id,
                m.menu_name,
                m.path,
                m.menu_type,
                m.menu_url,
                m.menu_icon_url,
                m.sort_no
            FROM sys_menu m
            INNER JOIN sys_role_menu rm ON rm.menu_id = m.id
            INNER JOIN sys_user_role ur ON ur.role_id = rm.role_id
            WHERE ur.user_id = #{userId}
              AND m.deleted = 0
            ORDER BY m.sort_no ASC, m.id ASC
            """)
    /*@Results(id = "sysMenuResultMap", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "gmt_modified", property = "gmtModified"),
            @Result(column = "gmt_created", property = "gmtCreated"),
            @Result(column = "created_by", property = "createdBy"),
            @Result(column = "modified_by", property = "modifiedBy"),
            @Result(column = "deleted", property = "deleted"),
            @Result(column = "parent_id", property = "parentId"),
            @Result(column = "menu_name", property = "menuName"),
            @Result(column = "path", property = "path"),
            @Result(column = "menu_type", property = "menuType"),
            @Result(column = "menu_url", property = "menuUrl"),
            @Result(column = "menu_icon_url", property = "menuIconUrl"),
            @Result(column = "sort_no", property = "sortNo")
    })*/
    List<SysMenu> findMenuCollByUserId(@Param("userId") Long userId);

    @Select("""
            SELECT DISTINCT
                m.id,
                m.gmt_modified,
                m.gmt_created,
                m.created_by,
                m.modified_by,
                m.deleted,
                m.parent_id,
                m.menu_name,
                m.path,
                m.menu_type,
                m.menu_url,
                m.menu_icon_url,
                m.sort_no
            FROM sys_menu m
            INNER JOIN sys_role_menu rm ON rm.menu_id = m.id
            WHERE rm.role_id = #{roleId}
              AND m.deleted = 0
            ORDER BY m.sort_no ASC, m.id ASC
            """)
    /*@Results(value = {
            @Result(column = "id", property = "id"),
            @Result(column = "gmt_modified", property = "gmtModified"),
            @Result(column = "gmt_created", property = "gmtCreated"),
            @Result(column = "created_by", property = "createdBy"),
            @Result(column = "modified_by", property = "modifiedBy"),
            @Result(column = "deleted", property = "deleted"),
            @Result(column = "parent_id", property = "parentId"),
            @Result(column = "menu_name", property = "menuName"),
            @Result(column = "path", property = "path"),
            @Result(column = "menu_type", property = "menuType"),
            @Result(column = "menu_url", property = "menuUrl"),
            @Result(column = "menu_icon_url", property = "menuIconUrl"),
            @Result(column = "sort_no", property = "sortNo")
    })*/
    List<SysMenu> findMenuCollByRoleId(@Param("roleId") Long roleId);


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
