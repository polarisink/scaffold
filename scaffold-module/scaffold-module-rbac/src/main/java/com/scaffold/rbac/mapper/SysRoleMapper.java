package com.scaffold.rbac.mapper;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scaffold.base.util.PageResponse;
import com.scaffold.orm.MyBaseMapper;
import com.scaffold.rbac.entity.SysRole;
import com.scaffold.rbac.vo.role.SysRolePageVO;

/**
 * 角色(SysRole)表Mapper接口
 *
 * @author aries
 * @since 2024-07-22 20:38:40
 */
public interface SysRoleMapper extends MyBaseMapper<SysRole> {
    /**
     * 单表分页
     * <p>
     * 通过角色名和编码模糊查询，更新时间倒序排列
     *
     * @param vo 分页面求
     * @return 分页结果
     */
    default PageResponse<SysRole> page(SysRolePageVO vo) {
        IPage<SysRole> page = selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()),
                Wrappers.<SysRole>lambdaQuery()
                        .like(StrUtil.isNotBlank(vo.getRoleName()), SysRole::getRoleName, vo.getRoleName())
                        .like(StrUtil.isNotBlank(vo.getRoleCode()), SysRole::getRoleCode, vo.getRoleCode())
                        .orderByDesc(SysRole::getGmtModified));
        return new PageResponse<>(page.getRecords(), page.getPages(), page.getCurrent(), page.getTotal(), page.getSize());
    }

    default boolean existsByRoleName(String roleName) {
        return exists(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleName, roleName));
    }

    default boolean existsByRoleCode(String roleCode) {
        return exists(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleCode, roleCode));
    }
}
