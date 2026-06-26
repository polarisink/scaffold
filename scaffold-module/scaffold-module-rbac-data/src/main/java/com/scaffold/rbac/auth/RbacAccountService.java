package com.scaffold.rbac.auth;

import com.scaffold.base.exception.BaseException;
import com.scaffold.rbac.entity.SysRole;
import com.scaffold.rbac.entity.SysUser;
import com.scaffold.rbac.mapper.SysRoleMapper;
import com.scaffold.rbac.mapper.SysUserMapper;
import com.scaffold.rbac.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RbacAccountService {
    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

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
        List<Long> roleIdList = userRoleMapper.selectRoleIdByUserId(userId);
        if (CollectionUtils.isEmpty(roleIdList)) {
            return List.of();
        }
        return roleMapper.selectBatchIds(roleIdList)
                .stream()
                .map(SysRole::getRoleCode)
                .toList();
    }
}
