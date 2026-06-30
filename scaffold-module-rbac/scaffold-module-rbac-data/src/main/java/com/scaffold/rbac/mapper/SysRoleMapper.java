package com.scaffold.rbac.mapper;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scaffold.orm.MyBaseMapper;
import com.scaffold.rbac.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色(SysRole)表Mapper接口
 *
 * @author aries
 * @since 2024-07-22 20:38:40
 */
@Mapper
public interface SysRoleMapper extends MyBaseMapper<SysRole> {
    default boolean existsByRoleName(String roleName) {
        return exists(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleName, roleName));
    }

    default boolean existsByRoleCode(String roleCode) {
        return exists(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleCode, roleCode));
    }
}
