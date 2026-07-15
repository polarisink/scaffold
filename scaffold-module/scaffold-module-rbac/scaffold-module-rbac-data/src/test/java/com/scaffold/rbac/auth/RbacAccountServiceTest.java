package com.scaffold.rbac.auth;

import com.scaffold.rbac.components.RbacCache;
import com.scaffold.rbac.entity.SysMenu;
import com.scaffold.rbac.entity.SysRole;
import com.scaffold.rbac.mapper.SysMenuMapper;
import com.scaffold.rbac.mapper.SysRoleMapper;
import com.scaffold.rbac.mapper.SysUserMapper;
import com.scaffold.rbac.mapper.SysUserRoleMapper;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RbacAccountServiceTest {

    private final SysUserMapper userMapper = mock(SysUserMapper.class);
    private final SysUserRoleMapper userRoleMapper = mock(SysUserRoleMapper.class);
    private final SysRoleMapper roleMapper = mock(SysRoleMapper.class);
    private final SysMenuMapper menuMapper = mock(SysMenuMapper.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final RbacCache rbacCache = mock(RbacCache.class);
    private final RbacAccountService accountService = new RbacAccountService(userMapper, passwordEncoder,rbacCache);

    @Test
    void shouldReturnDistinctNonBlankMenuPermissions() {
        SysMenu directory = menu(0, "/ignored");
        SysMenu first = menu(1, " user:list ");
        SysMenu duplicate = menu(1, "user:list");
        SysMenu second = menu(1, "user:edit");
        SysMenu blank = menu(1, " ");
        when(menuMapper.findMenuCollByUserId(7L))
                .thenReturn(List.of(directory, first, duplicate, second, blank));

        assertThat(accountService.selectPermissionCodeList(7L))
                .containsExactly("user:list", "user:edit");
    }

    @Test
    void shouldReturnDistinctNonBlankRoleCodes() {
        SysRole admin = role(" admin ");
        SysRole duplicate = role("admin");
        SysRole operator = role("operator");
        SysRole blank = role(" ");
        when(userRoleMapper.selectRoleIdByUserId(7L)).thenReturn(List.of(1L, 2L));
        when(roleMapper.selectByIds(List.of(1L, 2L)))
                .thenReturn(List.of(admin, duplicate, operator, blank));

        assertThat(rbacCache.selectRoleCodeList(7L))
                .containsExactly("admin", "operator");
    }

    private SysMenu menu(int type, String permission) {
        SysMenu menu = new SysMenu();
        menu.setMenuType(type);
        menu.setMenuUrl(permission);
        return menu;
    }

    private SysRole role(String roleCode) {
        SysRole role = new SysRole();
        role.setRoleCode(roleCode);
        return role;
    }
}
