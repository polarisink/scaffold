package com.scaffold.rbac.mapper;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scaffold.base.util.PageResponse;
import com.scaffold.orm.MyBaseMapper;
import com.scaffold.orm.starter.PageUtils;
import com.scaffold.rbac.entity.SysUser;
import com.scaffold.rbac.vo.user.SysUserPageVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

/**
 * (SysUser)表Mapper接口
 *
 * @author aries
 * @since 2024-07-22 20:40:08
 */
@Mapper
public interface SysUserMapper extends MyBaseMapper<SysUser> {

    /**
     * 通过名字查询是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    default boolean existsByUsername(String username) {
        return exists(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username));
    }


    /**
     * 通过名字查询用户及角色集合
     *
     * @param username 名字
     * @return 用户
     */
    default SysUser findByUsername(String username) {
        return selectOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username).last("limit 1"));
    }

    default Optional<SysUser> findOptionalByUsername(String username) {
        return selectList(Wrappers.<SysUser>lambdaQuery()
                        .eq(SysUser::getUsername, username))
                .stream()
                .findFirst();
    }

    /**
     * 通过组织id查下面的人列表
     *
     * @param orgId 组织id
     * @return 人员列表
     */
    default List<SysUser> selectByOrgId(Long orgId) {
        return selectList(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getOrgId, orgId));
    }

    default boolean existsByOrgId(Long orgId) {
        return exists(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getOrgId, orgId));
    }

    /**
     * 获取所有可用用户id
     *
     * @return 用户id集合
     */
    default List<Long> selectAllEnabledUserId() {
        return selectList(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getStatus, true).select(SysUser::getId)).stream().map(SysUser::getId).toList();
    }

    default PageResponse<SysUser> page(SysUserPageVO vo) {
        Page<SysUser> page = selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), Wrappers.<SysUser>lambdaQuery().like(StrUtil.isNotBlank(vo.getUsername()), SysUser::getUsername, vo.getUsername()).orderByDesc(SysUser::getGmtModified));
        return PageUtils.of(page);
    }

}
