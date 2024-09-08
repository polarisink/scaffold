package com.scaffold.biz.module.rbac.mapper;


import com.scaffold.biz.module.rbac.entity.SysRole;
import com.scaffold.biz.module.rbac.vo.role.SysRolePageVO;
import com.scaffold.core.base.util.PageResponse;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 角色(SysRole)表Mapper接口
 *
 * @author aries
 * @since 2024-07-22 20:38:40
 */

public interface SysRoleMapper extends JpaRepository<SysRole, Long> {
    /**
     * 单表分页
     * <p>
     * 通过角色名和编码模糊查询，更新时间倒序排列
     *
     * @param vo 分页面求
     * @return 分页结果
     */
    default PageResponse<SysRole> page(SysRolePageVO vo) {

        return null;
    }

    boolean existsByRoleName(String roleName);

    boolean existsByRoleCode(String roleCode);
}

