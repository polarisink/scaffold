package com.scaffold.rbac.service;

import com.scaffold.rbac.entity.SysMenu;
import com.scaffold.rbac.vo.menu.SysMenuCreateVO;
import com.scaffold.rbac.vo.menu.SysMenuUpdateVO;

import java.util.List;

/**
 * 菜单(SysMenu)表服务接口
 *
 * @author aries
 * @since 2024-07-22 20:38:40
 */
public interface SysMenuService {


    /**
     * 新增数据
     *
     * @param entity 实体对象
     * @return id
     */
    String save(SysMenuCreateVO entity);

    /**
     * 更新数据
     *
     * @param entity 实体对象
     */
    void updateById(SysMenuUpdateVO entity);

    /**
     * 通过ID删除数据
     *
     * @param id 主键ID
     */
    void deleteById(Long id);

    /**
     * 菜单树
     *
     * @return 树
     */
    List<SysMenu> tree();
}

