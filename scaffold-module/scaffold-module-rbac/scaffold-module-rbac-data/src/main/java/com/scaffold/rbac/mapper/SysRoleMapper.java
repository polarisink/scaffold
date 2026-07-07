package com.scaffold.rbac.mapper;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scaffold.base.util.PageResponse;
import com.scaffold.orm.MyBaseMapper;
import com.scaffold.orm.starter.PageUtils;
import com.scaffold.rbac.entity.SysRole;
import com.scaffold.rbac.vo.role.SysRolePageVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

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

    default Optional<SysRole> findByRoleCode(String roleCode) {
        return selectList(Wrappers.<SysRole>lambdaQuery()
                        .eq(SysRole::getRoleCode, roleCode))
                .stream()
                .findFirst();
    }

    default PageResponse<SysRole> page(SysRolePageVO vo) {
        IPage<SysRole> page = selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()),
                Wrappers.<SysRole>lambdaQuery()
                        .like(StrUtil.isNotBlank(vo.getRoleName()), SysRole::getRoleName, vo.getRoleName())
                        .like(StrUtil.isNotBlank(vo.getRoleCode()), SysRole::getRoleCode, vo.getRoleCode())
                        .orderByDesc(SysRole::getGmtModified));
        return PageUtils.of(page);
    }
}
