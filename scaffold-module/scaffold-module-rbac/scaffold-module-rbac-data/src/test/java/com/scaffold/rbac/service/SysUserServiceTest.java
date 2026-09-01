package com.scaffold.rbac.service;

import com.scaffold.base.util.PageResponse;
import com.scaffold.rbac.auth.RbacAccountService;
import com.scaffold.rbac.auth.RbacCurrentUser;
import com.scaffold.rbac.auth.RbacSessionRevoker;
import com.scaffold.rbac.components.RbacCache;
import com.scaffold.rbac.entity.SysRole;
import com.scaffold.rbac.entity.SysUser;
import com.scaffold.rbac.mapper.SysOrgMapper;
import com.scaffold.rbac.mapper.SysUserMapper;
import com.scaffold.rbac.mapper.SysUserRoleMapper;
import com.scaffold.rbac.vo.user.SysUserPageVO;
import com.scaffold.rbac.vo.user.SysUserUpdateVO;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class SysUserServiceTest {

    private final SysUserMapper userMapper = mock(SysUserMapper.class);
    private final SysUserRoleMapper userRoleMapper = mock(SysUserRoleMapper.class);
    private final RbacCache cache = mock(RbacCache.class);
    private final RbacAccountService accountService = mock(RbacAccountService.class);
    private final RbacCurrentUser currentUser = mock(RbacCurrentUser.class);
    private final RbacSessionRevoker sessionRevoker = mock(RbacSessionRevoker.class);
    private final SysOrgMapper orgMapper = mock(SysOrgMapper.class);
    private final SysUserService userService =
            new SysUserService(userMapper, userRoleMapper, cache, accountService, currentUser, sessionRevoker, orgMapper);

    @Test
    void shouldIncludeRolesInUserPage() {
        SysUser user = new SysUser();
        user.setId(8L);
        user.setUsername("alice");
        SysRole role = new SysRole("管理员", "admin");
        role.setId(2L);
        SysUserPageVO request = new SysUserPageVO();
        when(userMapper.page(request)).thenReturn(new PageResponse<>(List.of(user), 1, 1, 1, 10));
        when(cache.roles(8L)).thenReturn(List.of(role));

        var result = userService.page(request);

        assertThat(result.records()).singleElement().satisfies(record -> {
            assertThat(record.getId()).isEqualTo(8L);
            assertThat(record.getUsername()).isEqualTo("alice");
            assertThat(record.getRoles()).containsExactly(role);
        });
    }

    @Test
    void shouldRejectBanningCurrentUser() {
        when(currentUser.userId()).thenReturn(7L);

        assertThatThrownBy(() -> userService.ban(7L))
                .hasMessageContaining("不能封禁自己");

        verifyNoInteractions(userMapper);
        verify(sessionRevoker, never()).revokeUserSessions(anyLong());
    }

    @Test
    void shouldToggleUserStatusAndRevokeSessions() {
        SysUser user = new SysUser();
        user.setId(8L);
        user.setStatus(true);
        when(currentUser.userId()).thenReturn(7L);
        when(userMapper.selectById(8L)).thenReturn(user);

        userService.ban(8L);

        ArgumentCaptor<SysUser> userCaptor = ArgumentCaptor.forClass(SysUser.class);
        verify(userMapper).updateById(userCaptor.capture());
        org.assertj.core.api.Assertions.assertThat(userCaptor.getValue().getStatus()).isFalse();
        verify(sessionRevoker).revokeUserSessions(8L);
    }

    @Test
    void shouldInsertOnlyNewRolesWhenUpdatingUser() {
        SysUser user = new SysUser();
        user.setId(8L);
        user.setUsername("alice");
        SysUserUpdateVO request = new SysUserUpdateVO();
        request.setId(8L);
        request.setUsername("alice");
        request.setOrgId(2L);
        request.setRoleIdList(List.of(1L, 2L));
        when(userMapper.selectById(8L)).thenReturn(user);
        when(orgMapper.selectById(2L)).thenReturn(new com.scaffold.rbac.entity.SysOrg());
        when(userRoleMapper.selectRoleIdByUserId(8L)).thenReturn(List.of(1L));

        userService.updateById(request);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<com.scaffold.rbac.entity.SysUserRole>> rolesCaptor =
                ArgumentCaptor.forClass(Collection.class);
        verify(userRoleMapper).insertBatchSomeColumn(rolesCaptor.capture());
        org.assertj.core.api.Assertions.assertThat(rolesCaptor.getValue())
                .extracting(com.scaffold.rbac.entity.SysUserRole::getRoleId)
                .containsExactly(2L);
        verify(cache).userClear(8L);
    }
}
