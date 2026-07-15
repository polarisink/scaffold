package com.scaffold.rbac.auth;

import com.scaffold.base.exception.BaseException;
import com.scaffold.rbac.components.RbacCache;
import com.scaffold.rbac.entity.SysUser;
import com.scaffold.rbac.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RbacAccountService {
    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RbacCache rbacCache;

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

    public List<String> selectRoleCodeList(Long userId) {
        return rbacCache.selectRoleCodeList(userId);
    }

    /**
     * 查询用户可访问的菜单权限标识。
     * 当前数据模型以菜单 URL 作为权限标识，目录节点不参与鉴权。
     */
    public List<String> selectPermissionCodeList(Long userId) {
        return rbacCache.selectPermissionCodeList(userId);
    }
}
