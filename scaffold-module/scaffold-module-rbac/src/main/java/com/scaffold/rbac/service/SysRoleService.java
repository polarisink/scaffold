package com.scaffold.rbac.service;

import com.scaffold.base.util.PageResponse;
import com.scaffold.rbac.entity.SysRole;
import com.scaffold.rbac.vo.menu.SysRoleWrapper;
import com.scaffold.rbac.vo.role.SysRoleCreateVO;
import com.scaffold.rbac.vo.role.SysRolePageVO;
import com.scaffold.rbac.vo.role.SysRoleUpdateVO;

/**
 * 角色(SysRole)表服务接口
 *
 * @author aries
 * @since 2024-07-22 20:38:40
 */
public interface SysRoleService {

    /**
     * 分页查询所有数据
     *
     * @param req 分页请求
     * @return 分页数据
     */
    PageResponse<SysRole> page(SysRolePageVO req);

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键ID
     * @return 单条数据
     */
    SysRoleWrapper roleWrapper(Long id);

    /**
     * 新增数据
     *
     * @param entity 实体对象
     * @return id
     */
    Long save(SysRoleCreateVO entity);

    /**
     * 更新数据
     *
     * @param entity 实体对象
     */
    void updateById(SysRoleUpdateVO entity);

    /**
     * 通过ID删除数据
     *
     * @param id 主键ID
     */
    void deleteById(Long id);
}

