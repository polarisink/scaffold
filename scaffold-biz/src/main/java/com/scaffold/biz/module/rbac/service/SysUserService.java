package com.scaffold.biz.module.rbac.service;

import com.scaffold.biz.module.rbac.entity.SysUser;
import com.scaffold.biz.module.rbac.module.vo.user.*;
import com.scaffold.biz.module.rbac.vo.user.*;
import com.scaffold.core.base.util.PageResponse;

/**
 * (SysUser)表服务接口
 *
 * @author aries
 * @since 2024-07-22 20:40:08
 */
public interface SysUserService {

    /**
     * 分页查询所有数据
     *
     * @param req 分页请求
     * @return 分页数据
     */
    PageResponse<SysUser> page(SysUserPageVO req);

    /**
     * 新增数据
     *
     * @param entity 实体对象
     * @return id
     */
    String save(SysUserCreateVO entity);

    /**
     * 更新数据
     *
     * @param entity 实体对象
     */
    void updateById(SysUserUpdateVO entity);

    /**
     * 通过ID删除数据
     *
     * @param id 主键ID
     */
    void deleteById(Long id);

    /**
     * 用户信息
     *
     * @return 用户信息
     */
    SysUserInfo userInfo();

    /**
     * 用户更新密码
     *
     * @param passwdUpdateVO 密码更新请求
     */
    void updatePasswd(PasswdUpdateVO passwdUpdateVO);

    /**
     * 重置密码
     *
     * @param userId 用户id
     */
    void resetPasswd(Long userId);

    /**
     * 封禁用户
     *
     * @param userId 用户id
     */
    void ban(Long userId);

}

