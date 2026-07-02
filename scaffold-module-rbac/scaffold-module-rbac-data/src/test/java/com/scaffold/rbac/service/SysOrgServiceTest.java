package com.scaffold.rbac.service;

import com.scaffold.base.exception.BaseException;
import com.scaffold.rbac.components.RbacCache;
import com.scaffold.rbac.entity.SysOrg;
import com.scaffold.rbac.mapper.SysOrgMapper;
import com.scaffold.rbac.mapper.SysUserMapper;
import com.scaffold.rbac.vo.org.SysOrgCreateVO;
import com.scaffold.rbac.vo.org.SysOrgUpdateVO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SysOrgServiceTest {

    private final SysOrgMapper orgMapper = mock(SysOrgMapper.class);
    private final SysUserMapper userMapper = mock(SysUserMapper.class);
    private final RbacCache rbacCache = mock(RbacCache.class);
    private final SysOrgService service = new SysOrgService(orgMapper, userMapper, rbacCache);

    @Test
    void createsRootOrganizationAndClearsTreeCache() {
        when(orgMapper.countByParentId(0L)).thenReturn(2L);
        doAnswer(invocation -> {
            SysOrg org = invocation.getArgument(0);
            org.setId(42L);
            return 1;
        }).when(orgMapper).insert(any(SysOrg.class));

        Long id = service.save(new SysOrgCreateVO(0L, "研发中心", "RND", null));

        assertThat(id).isEqualTo(42L);
        verify(orgMapper).insert(any(SysOrg.class));
        verify(rbacCache).orgClear();
    }

    @Test
    void rejectsMovingOrganizationBelowItsOwnDescendant() {
        SysOrg current = org(2L, 0L, "研发中心", "RND");
        SysOrg child = org(3L, 2L, "平台部", "PLATFORM");
        when(orgMapper.selectById(2L)).thenReturn(current);
        when(orgMapper.selectById(3L)).thenReturn(child);

        assertThatThrownBy(() -> service.updateById(
                new SysOrgUpdateVO(2L, 3L, "研发中心", "RND", 0)))
                .isInstanceOf(BaseException.class)
                .hasMessage("组织层级不能形成循环");

        verify(orgMapper, never()).updateById(any(SysOrg.class));
    }

    @Test
    void rejectsDeletingOrganizationThatStillHasUsers() {
        SysOrg current = org(2L, 0L, "研发中心", "RND");
        when(orgMapper.selectById(2L)).thenReturn(current);
        when(userMapper.existsByOrgId(2L)).thenReturn(true);

        assertThatThrownBy(() -> service.deleteById(2L))
                .isInstanceOf(BaseException.class)
                .hasMessage("该组织下还有人员未移除");

        verify(orgMapper, never()).deleteById(2L);
    }

    private static SysOrg org(Long id, Long parentId, String name, String code) {
        SysOrg org = new SysOrg();
        org.setId(id);
        org.setParentId(parentId);
        org.setOrgName(name);
        org.setOrgCode(code);
        return org;
    }
}
