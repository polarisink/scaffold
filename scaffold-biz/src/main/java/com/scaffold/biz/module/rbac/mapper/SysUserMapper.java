package com.scaffold.biz.module.rbac.mapper;


import cn.hutool.extra.spring.SpringUtil;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.scaffold.biz.module.rbac.entity.SysUser;
import com.scaffold.biz.module.rbac.vo.user.SysUserPageVO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * (SysUser)表Mapper接口
 *
 * @author aries
 * @since 2024-07-22 20:40:08
 */

public interface SysUserMapper extends JpaRepository<SysUser, Long> {
    /**
     * 通过vo查询，用于分页
     *
     * @param vo vo
     * @return list
     */
    default List<SysUser> list(SysUserPageVO vo) {
        EasyEntityQuery query = SpringUtil.getBean(EasyEntityQuery.class);
        return query.queryable(SysUser.class).toList();
    }


    /**
     * 通过名字查询是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);


    /**
     * 通过名字查询用户及角色集合
     *
     * @param username 名字
     * @return 用户
     */
    default SysUser findByUsername(String username) {
        return null;
    }

    /**
     * 通过组织id查下面的人列表
     *
     * @param orgId 组织id
     * @return 人员列表
     */
    default List<SysUser> selectByOrgId(String orgId) {
        return null;
    }

    /**
     * 获取所有可用用户id
     *
     * @return 用户id集合
     */
    default List<Long> selectAllEnabledUserId() {
        return null;
    }

}

