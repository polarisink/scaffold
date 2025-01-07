package com.scaffold.biz.module.rbac.mapper;


import com.scaffold.biz.module.rbac.entity.SysUserRole;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * 用户角色表(SysUserRole)表Mapper接口
 *
 * @author aries
 * @since 2024-07-22 20:38:42
 */
public interface SysUserRoleMapper extends JpaRepository<SysUserRole, Long> {
    /**
     * 通过用户id获取角色id集合
     *
     * @param userId 用户id,不能为空
     * @return 角色id集合
     */
    default List<Long> selectRoleIdByUserId(@NotNull Long userId) {
        return null;
    }

    /**
     * 通过用户id和角色id集合删除
     *
     * @param userId     用户id
     * @param roleIdColl 角色id集合,为空时不加此条件
     */
    default void deleteByUserIdAndRoleIdIn(@NotNull Long userId, @Nullable Collection<Long> roleIdColl) {
    }

    /**
     * 通过角色id查询用户id
     *
     * @param roleIdColl 角色id集合
     * @return 用户id集合
     */
    default List<Long> selectUserIdByRoleIdIn(@NotNull Collection<Long> roleIdColl) {
        return null;
    }

    /**
     * 通过角色id查询是否存在
     *
     * @param roleId 角色id
     * @return 是否存在
     */
    default boolean existsByRoleId(Long roleId) {
        return true;
    }

    /*
     *//**
     * 通过用户id和角色id集合删除
     * <p>
     * 因为in性能差一点，所以多写了一个，不然跟下面的进行服用也可以
     *
     * @param userIdColl 用户id
     * @param roleIdColl 角色id集合,为空时不加此条件
     *//*
    default void deleteByUserIdInAndRoleIdIn(@NotNull Collection<String> userIdColl, @Nullable Collection<String> roleIdColl) {
        LambdaQueryWrapper<SysUserRole> w = new LambdaQueryWrapper<SysUserRole>()
                .in(SysUserRole::getUserId, userIdColl)
                .in(roleIdColl != null && !roleIdColl.isEmpty(), SysUserRole::getRoleId, roleIdColl);
        delete(w);
    }*/
}

