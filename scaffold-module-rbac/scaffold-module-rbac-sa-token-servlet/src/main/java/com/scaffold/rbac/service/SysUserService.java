package com.scaffold.rbac.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import com.scaffold.base.exception.Assert;
import com.scaffold.base.util.CollUtils;
import com.scaffold.base.util.PageResponse;
import com.scaffold.rbac.auth.RbacAccountService;
import com.scaffold.rbac.components.PasswordFactory;
import com.scaffold.rbac.components.RbacCache;
import com.scaffold.rbac.components.SaRbacCurrentUser;
import com.scaffold.rbac.contant.RbacResultEnum;
import com.scaffold.rbac.entity.SysMenu;
import com.scaffold.rbac.entity.SysRole;
import com.scaffold.rbac.entity.SysUser;
import com.scaffold.rbac.entity.SysUserRole;
import com.scaffold.rbac.mapper.SysUserMapper;
import com.scaffold.rbac.mapper.SysUserRoleMapper;
import com.scaffold.rbac.mapper.SysOrgMapper;
import com.scaffold.rbac.vo.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SysUserService implements ISysUserService {
    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final RbacCache rbacCache;
    private final RbacAccountService accountService;
    private final SysOrgMapper sysOrgMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SysUser> page(SysUserPageVO vo) {
        return sysUserMapper.page(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(SysUserCreateVO vo) {
        RbacResultEnum.UNIQUE_USERNAME.isFalse(sysUserMapper.existsByUsername(vo.username()));
        RbacResultEnum.ORG_NOT_FOUND.notNull(sysOrgMapper.selectById(vo.orgId()));
        SysUser entity = new SysUser();
        BeanUtils.copyProperties(vo, entity);
        entity.setPassword(PasswordFactory.encode(vo.password()));
        sysUserMapper.insert(entity);
        Long userId = entity.getId();
        List<SysUserRole> userRoleList = CollUtils.toList(vo.roleIdList(), roleId -> new SysUserRole(userId, roleId));
        sysUserRoleMapper.insertBatchSomeColumn(userRoleList);
        return userId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateById(SysUserUpdateVO vo) {
        Long userId = vo.getId();
        SysUser entity = sysUserMapper.selectById(userId);
        boolean uniqueName = Objects.equals(vo.getUsername(), entity.getUsername()) || !sysUserMapper.existsByUsername(vo.getUsername());
        RbacResultEnum.UNIQUE_USERNAME.isTrue(uniqueName);
        RbacResultEnum.ORG_NOT_FOUND.notNull(sysOrgMapper.selectById(vo.getOrgId()));
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long userId) {
        Long currentUserId = SaRbacCurrentUser.userId();
        Assert.notEquals(currentUserId,userId,"不能删除自己");
        sysUserMapper.deleteById(userId);
        sysUserRoleMapper.deleteByUserIdAndRoleIdIn(userId, null);
        rbacCache.userClear(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public SysUserInfo userInfo(Long userId) {
        if (userId == null) {
            userId = SaRbacCurrentUser.userId();
        }
        SysUser user = sysUserMapper.selectById(userId);
        Assert.notNull(user, "当前用户不存在");
        List<SysMenu> menus = rbacCache.userTree(userId);
        List<SysRole> roles = rbacCache.roles(userId);
        return new SysUserInfo(user, sysOrgMapper.selectById(user.getOrgId()), roles, menus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePasswd(PasswdUpdateVO vo) {
        RbacResultEnum.PASSWD_NOT_CHANGE.isFalse(Objects.equals(vo.oldPasswd(), vo.newPasswd()));
        accountService.login(SaRbacCurrentUser.username(), vo.oldPasswd());
        Long userId = SaRbacCurrentUser.userId();
        SysUser user = sysUserMapper.selectById(userId);
        user.setPassword(PasswordFactory.encode(vo.newPasswd()));
        sysUserMapper.updateById(user);
        StpUtil.logout(userId);
    }

    @Override
    public void resetPasswd(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        user.setPassword(PasswordFactory.reset(user.getUsername()));
        sysUserMapper.updateById(user);
        StpUtil.logout(userId);
    }

    @Override
    public void ban(Long userId) {
        Long currentUserId = SaRbacCurrentUser.userId();
        Assert.notEquals(currentUserId,userId,"不能封禁自己");
        RbacResultEnum.CAN_NOT_BAN_MYSELF.notEquals(userId, SaRbacCurrentUser.userId());
        SysUser user = sysUserMapper.selectById(userId);
        user.setStatus(!user.getStatus());
        sysUserMapper.updateById(user);
        StpUtil.logout(userId);
    }
}
