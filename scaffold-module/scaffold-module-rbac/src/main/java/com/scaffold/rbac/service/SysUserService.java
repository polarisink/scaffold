package com.scaffold.rbac.service;

import cn.hutool.core.collection.CollUtil;
import com.scaffold.base.util.CollUtils;
import com.scaffold.base.util.PageResponse;
import com.scaffold.rbac.components.PasswordFactory;
import com.scaffold.rbac.components.RbacCache;
import com.scaffold.rbac.contant.RbacResultEnum;
import com.scaffold.rbac.entity.SysMenu;
import com.scaffold.rbac.entity.SysUser;
import com.scaffold.rbac.entity.SysUserRole;
import com.scaffold.rbac.mapper.SysUserMapper;
import com.scaffold.rbac.mapper.SysUserRoleMapper;
import com.scaffold.rbac.vo.user.PasswdUpdateVO;
import com.scaffold.rbac.vo.user.SysUserCreateVO;
import com.scaffold.rbac.vo.user.SysUserInfo;
import com.scaffold.rbac.vo.user.SysUserPageVO;
import com.scaffold.rbac.vo.user.SysUserUpdateVO;
import com.scaffold.security.config.TokenService;
import com.scaffold.security.vo.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SysUserService {
    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final RbacCache rbacCache;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Transactional(readOnly = true)
    public PageResponse<SysUser> page(SysUserPageVO vo) {
        return sysUserMapper.page(vo);
    }

    @Transactional(rollbackFor = Exception.class)
    public String save(SysUserCreateVO vo) {
        RbacResultEnum.UNIQUE_USERNAME.isFalse(sysUserMapper.existsByUsername(vo.username()));
        SysUser entity = new SysUser();
        BeanUtils.copyProperties(vo, entity);
        entity.setPassword(PasswordFactory.encode(vo.password()));
        sysUserMapper.insert(entity);
        Long userId = entity.getId();
        List<SysUserRole> userRoleList = CollUtils.toList(vo.roleIdList(), roleId -> new SysUserRole(userId, roleId));
        sysUserRoleMapper.insertBatchSomeColumn(userRoleList);
        return userId.toString();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateById(SysUserUpdateVO vo) {
        Long userId = vo.getId();
        SysUser entity = sysUserMapper.selectById(userId);
        boolean uniqueName = Objects.equals(vo.getUsername(), entity.getUsername()) || !sysUserMapper.existsByUsername(vo.getUsername());
        RbacResultEnum.UNIQUE_USERNAME.isTrue(uniqueName);
        BeanUtils.copyProperties(vo, entity);
        sysUserMapper.updateById(entity);
        List<Long> newRoleIdList = vo.getRoleIdList();
        List<Long> roleIdListIdDb = sysUserRoleMapper.selectRoleIdByUserId(userId);
        Collections.sort(newRoleIdList);
        if (Objects.equals(newRoleIdList, roleIdListIdDb)) {
            return;
        }
        Collection<Long> addRoleIdList = CollUtil.subtract(newRoleIdList, roleIdListIdDb);
        if (!addRoleIdList.isEmpty()) {
            List<SysUserRole> addUrList = CollUtils.toList(vo.getRoleIdList(), roleId -> new SysUserRole(userId, roleId));
            sysUserRoleMapper.insertBatchSomeColumn(addUrList);
        }
        Collection<Long> deleteRoleIdList = CollUtil.subtract(roleIdListIdDb, newRoleIdList);
        if (!deleteRoleIdList.isEmpty()) {
            sysUserRoleMapper.deleteByUserIdAndRoleIdIn(userId, deleteRoleIdList);
        }
        rbacCache.userClear(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long userId) {
        sysUserMapper.deleteById(userId);
        sysUserRoleMapper.deleteByUserIdAndRoleIdIn(userId, null);
        rbacCache.userClear(userId);
    }

    @Transactional(readOnly = true)
    public SysUserInfo userInfo() {
        SysUser user = sysUserMapper.findByUsername(LoginUser.username());
        List<SysMenu> menus = rbacCache.userTree(LoginUser.userId());
        return new SysUserInfo(user, menus);
    }

    public void updatePasswd(PasswdUpdateVO vo) {
        RbacResultEnum.PASSWD_NOT_CHANGE.isFalse(Objects.equals(vo.oldPasswd(), vo.newPasswd()));
        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(LoginUser.username(), vo.oldPasswd());
        authenticationManager.authenticate(authenticationToken);
        Long userId = LoginUser.userId();
        SysUser user = sysUserMapper.selectById(userId);
        user.setPassword(PasswordFactory.encode(vo.newPasswd()));
        sysUserMapper.updateById(user);
        tokenService.del(userId);
    }

    public void resetPasswd(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        user.setPassword(PasswordFactory.reset(user.getUsername()));
        sysUserMapper.updateById(user);
        tokenService.del(userId);
    }

    public void ban(Long userId) {
        RbacResultEnum.CAN_NOT_BAN_MYSELF.notEquals(userId, LoginUser.userId());
        SysUser user = sysUserMapper.selectById(userId);
        user.setStatus(!user.getStatus());
        sysUserMapper.updateById(user);
        tokenService.del(userId);
    }
}
