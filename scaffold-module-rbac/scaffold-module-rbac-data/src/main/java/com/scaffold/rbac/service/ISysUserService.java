package com.scaffold.rbac.service;

import com.scaffold.base.util.PageResponse;
import com.scaffold.rbac.entity.SysUser;
import com.scaffold.rbac.vo.user.*;

/**
 * 用户接口
 */
public interface ISysUserService {

    /**
     * 用户分页
     *
     * @param vo 请求
     * @return 分页结果
     */
    PageResponse<SysUser> page(SysUserPageVO vo);

    /**
     * 新增用户
     *
     * @param vo 请求
     * @return 新增用户的id
     */
    Long save(SysUserCreateVO vo);

    /**
     * 更新用户
     *
     * @param vo 请求
     */
    void updateById(SysUserUpdateVO vo);

    /**
     * 删除用户
     *
     * @param userId 用户id
     */
    void deleteById(Long userId);

    /**
     * 用户信息
     *
     * @param userId 用户id，如果为空就获取用户id
     * @return 用户信息
     */
    SysUserInfo userInfo(Long userId);

    /**
     * 更新密码
     *
     * @param vo 请求
     */
    void updatePasswd(PasswdUpdateVO vo);

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