package com.scaffold.rbac.auth;

import com.scaffold.base.exception.BaseException;
import com.scaffold.rbac.entity.SysMenu;
import com.scaffold.rbac.entity.SysRole;
import com.scaffold.rbac.entity.SysUser;
import com.scaffold.rbac.mapper.SysMenuMapper;
import com.scaffold.rbac.mapper.SysRoleMapper;
import com.scaffold.rbac.mapper.SysUserMapper;
import com.scaffold.rbac.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.scaffold.rbac.contant.RbacCacheConst.USER_PERMISSIONS_CACHE;
import static com.scaffold.rbac.contant.RbacCacheConst.USER_ROLES_CACHE;

@Service
@RequiredArgsConstructor
public class RbacAccountService {
    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;
    private final SysMenuMapper menuMapper;
    private final PasswordEncoder passwordEncoder;

    public RbacLoginUser login(String username, String password) {
        SysUser user = userMapper.findByUsername(username);
        if (user == null) {
            throw new BaseException("用户不存在");
        }
        if (!Boolean.TRUE.equals(user.getStatus())) {
            throw new BaseException("用户被封禁，请联系管理员");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BaseException("用户名或密码错误");
        }
        return new RbacLoginUser(user.getId(), user.getUsername(), selectRoleCodeList(user.getId()));
    }

    @Cacheable(cacheNames = USER_ROLES_CACHE, key = "#userId")
    public List<String> selectRoleCodeList(Long userId) {
        List<Long> roleIdList = userRoleMapper.selectRoleIdByUserId(userId);
        if (CollectionUtils.isEmpty(roleIdList)) {
            return new ArrayList<>();
        }
        return roleMapper.selectByIds(roleIdList)
                .stream()
                .map(SysRole::getRoleCode)
                .filter(StringUtils::hasText)
                .map(String::trim)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * 查询用户可访问的菜单权限标识。
     * 当前数据模型以菜单 URL 作为权限标识，目录节点不参与鉴权。
     */
    @Cacheable(cacheNames = USER_PERMISSIONS_CACHE, key = "#userId")
    public List<String> selectPermissionCodeList(Long userId) {
        List<SysMenu> menuList = menuMapper.findMenuCollByUserId(userId);
        if (CollectionUtils.isEmpty(menuList)) {
            return new ArrayList<>();
        }
        return menuList.stream()
                .filter(menu -> Integer.valueOf(1).equals(menu.getMenuType()))
                .map(SysMenu::getMenuUrl)
                .filter(StringUtils::hasText)
                .map(String::trim)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
