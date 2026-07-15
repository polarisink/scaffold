package com.scaffold.rbac.service;

import cn.hutool.core.collection.CollUtil;
import com.scaffold.base.exception.Assert;
import com.scaffold.base.util.CollUtils;
import com.scaffold.base.util.PageResponse;
import com.scaffold.rbac.auth.RbacAccountService;
import com.scaffold.rbac.auth.RbacCurrentUser;
import com.scaffold.rbac.auth.RbacSessionRevoker;
import com.scaffold.rbac.components.PasswordFactory;
import com.scaffold.rbac.components.RbacCache;
import com.scaffold.rbac.contant.RbacResultEnum;
import com.scaffold.rbac.entity.SysMenu;
import com.scaffold.rbac.entity.SysRole;
import com.scaffold.rbac.entity.SysUser;
import com.scaffold.rbac.entity.SysUserRole;
import com.scaffold.rbac.mapper.SysOrgMapper;
import com.scaffold.rbac.mapper.SysUserMapper;
import com.scaffold.rbac.mapper.SysUserRoleMapper;
import com.scaffold.rbac.vo.user.PasswdUpdateVO;
import com.scaffold.rbac.vo.user.SysUserCreateVO;
import com.scaffold.rbac.vo.user.SysUserInfo;
import com.scaffold.rbac.vo.user.SysUserPageVO;
import com.scaffold.rbac.vo.user.SysUserUpdateVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SysUserService  {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final RbacCache rbacCache;
    private final RbacAccountService accountService;
    private final RbacCurrentUser currentUser;
    private final RbacSessionRevoker sessionRevoker;
    private final SysOrgMapper sysOrgMapper;

    @Transactional(readOnly = true)
    public PageResponse<SysUser> page(SysUserPageVO vo) {
        return sysUserMapper.page(vo);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long save(SysUserCreateVO vo) {
        RbacResultEnum.UNIQUE_USERNAME.isFalse(sysUserMapper.existsByUsername(vo.username()));
        RbacResultEnum.ORG_NOT_FOUND.notNull(sysOrgMapper.selectById(vo.orgId()));
        SysUser entity = new SysUser();
        BeanUtils.copyProperties(vo, entity);
        entity.setPassword(PasswordFactory.encode(vo.password()));
        sysUserMapper.insert(entity);
        Long userId = entity.getId();
        List<SysUserRole> roles = CollUtils.toList(vo.roleIdList(), roleId -> new SysUserRole(userId, roleId));
        if (!roles.isEmpty()) {
            sysUserRoleMapper.insertBatchSomeColumn(roles);
        }
        return userId;
    }

    
    @Transactional(rollbackFor = Exception.class)
    public void updateById(SysUserUpdateVO vo) {
        Long userId = vo.getId();
        SysUser entity = sysUserMapper.selectById(userId);
        Assert.notNull(entity, "用户不存在");
        boolean uniqueName = Objects.equals(vo.getUsername(), entity.getUsername())
                || !sysUserMapper.existsByUsername(vo.getUsername());
        RbacResultEnum.UNIQUE_USERNAME.isTrue(uniqueName);
        RbacResultEnum.ORG_NOT_FOUND.notNull(sysOrgMapper.selectById(vo.getOrgId()));
        BeanUtils.copyProperties(vo, entity);
        sysUserMapper.updateById(entity);

        List<Long> requestedRoleIds = vo.getRoleIdList() == null ? List.of() : vo.getRoleIdList();
        List<Long> storedRoleIds = sysUserRoleMapper.selectRoleIdByUserId(userId);
        if (new HashSet<>(requestedRoleIds).equals(new HashSet<>(storedRoleIds))) {
            return;
        }
        Collection<Long> addedRoleIds = CollUtil.subtract(requestedRoleIds, storedRoleIds);
        if (!addedRoleIds.isEmpty()) {
            List<SysUserRole> addedRoles = CollUtils.toList(addedRoleIds, roleId -> new SysUserRole(userId, roleId));
            sysUserRoleMapper.insertBatchSomeColumn(addedRoles);
        }
        Collection<Long> deletedRoleIds = CollUtil.subtract(storedRoleIds, requestedRoleIds);
        if (!deletedRoleIds.isEmpty()) {
            sysUserRoleMapper.deleteByUserIdAndRoleIdIn(userId, deletedRoleIds);
        }
        rbacCache.userClear(userId);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long userId) {
        Assert.notEquals(currentUser.userId(), userId, "不能删除自己");
        sysUserMapper.deleteById(userId);
        sysUserRoleMapper.deleteByUserIdAndRoleIdIn(userId, null);
        rbacCache.userClear(userId);
        sessionRevoker.revokeUserSessions(userId);
    }

    
    @Transactional(readOnly = true)
    public SysUserInfo userInfo(Long userId) {
        Long resolvedUserId = userId == null ? currentUser.userId() : userId;
        SysUser user = sysUserMapper.selectById(resolvedUserId);
        Assert.notNull(user, "当前用户不存在");
        List<SysMenu> menus = rbacCache.userTree(resolvedUserId);
        List<SysRole> roles = rbacCache.roles(resolvedUserId);
        return new SysUserInfo(user, sysOrgMapper.selectById(user.getOrgId()), roles, menus);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public void updatePasswd(PasswdUpdateVO vo) {
        RbacResultEnum.PASSWD_NOT_CHANGE.isFalse(Objects.equals(vo.oldPasswd(), vo.newPasswd()));
        accountService.login(currentUser.username(), vo.oldPasswd());
        Long userId = currentUser.userId();
        SysUser user = sysUserMapper.selectById(userId);
        Assert.notNull(user, "当前用户不存在");
        user.setPassword(PasswordFactory.encode(vo.newPasswd()));
        sysUserMapper.updateById(user);
        sessionRevoker.revokeUserSessions(userId);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public void resetPasswd(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        Assert.notNull(user, "用户不存在");
        user.setPassword(PasswordFactory.reset(user.getUsername()));
        sysUserMapper.updateById(user);
        sessionRevoker.revokeUserSessions(userId);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public void ban(Long userId) {
        RbacResultEnum.CAN_NOT_BAN_MYSELF.notEquals(userId, currentUser.userId());
        SysUser user = sysUserMapper.selectById(userId);
        Assert.notNull(user, "用户不存在");
        user.setStatus(!user.getStatus());
        sysUserMapper.updateById(user);
        sessionRevoker.revokeUserSessions(userId);
    }
}
