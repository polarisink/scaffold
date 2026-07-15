package com.scaffold.rbac.auth;

import com.scaffold.rbac.components.RbacCache;
import com.scaffold.rbac.mapper.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RbacAccountServiceTest {

    private final SysUserMapper userMapper = mock(SysUserMapper.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final RbacCache rbacCache = mock(RbacCache.class);
    private final RbacAccountService accountService = new RbacAccountService(userMapper, passwordEncoder,rbacCache);

    @Test
    void shouldReturnCachedMenuPermissions() {
        when(rbacCache.selectPermissionCodeList(7L)).thenReturn(List.of("user:list", "user:edit"));

        assertThat(accountService.selectPermissionCodeList(7L))
                .containsExactly("user:list", "user:edit");
    }

    @Test
    void shouldReturnCachedRoleCodes() {
        when(rbacCache.selectRoleCodeList(7L)).thenReturn(List.of("admin", "operator"));

        assertThat(accountService.selectRoleCodeList(7L))
                .containsExactly("admin", "operator");
    }
}
