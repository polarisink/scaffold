package com.scaffold.rbac.components;

import com.scaffold.rbac.entity.SysUser;
import com.scaffold.rbac.mapper.SysUserMapper;
import com.scaffold.security.vo.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final SysUserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser u = userMapper.findByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        if (!u.getStatus()) {
            throw new DisabledException("用户被封禁");
        }
        return new LoginUser(u.getId(), u.getUsername(), u.getPassword(), null/*CollUtils.toList(u.getRoleList(), RoleVO::getRoleCode)*/);
    }
}
