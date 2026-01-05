package com.scaffold.rbac.service.impl;

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
import com.scaffold.rbac.service.SysUserService;
import com.scaffold.rbac.vo.user.*;
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

/**
 * (SysUser)表服务实现类
 *
 * @author aries
 * @since 2024-07-22 20:40:08
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {
    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final RbacCache rbacCache;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SysUser> page(SysUserPageVO vo) {
        return null;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String save(SysUserCreateVO vo) {
        //用户不能重名
        RbacResultEnum.UNIQUE_USERNAME.isFalse(sysUserMapper.existsByUsername(vo.username()));
        SysUser entity = new SysUser();
        BeanUtils.copyProperties(vo, entity);
        //密码加密
        entity.setPassword(PasswordFactory.encode(vo.password()));
        sysUserMapper.saveAndFlush(entity);
        Long userId = entity.getId();
        //保存角色
        List<SysUserRole> userRoleList = CollUtils.toList(vo.roleIdList(), roleId -> new SysUserRole(userId, roleId));
        sysUserRoleMapper.saveAllAndFlush(userRoleList);
        //加入角色的菜单缓存
        //menuCache.userTree(userId);
        return userId.toString();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateById(SysUserUpdateVO vo) {
        Long userId = vo.getId();
        SysUser entity = sysUserMapper.findById(userId).orElse(null);
        boolean uniqueName = Objects.equals(vo.getUsername(), entity.getUsername()) || !sysUserMapper.existsByUsername(vo.getUsername());
        RbacResultEnum.UNIQUE_USERNAME.isTrue(uniqueName);
        //更新用户
        BeanUtils.copyProperties(vo, entity);
        sysUserMapper.save(entity);
        //更新角色
        List<Long> newRoleIdList = vo.getRoleIdList();
        List<Long> roleIdListIdDb = sysUserRoleMapper.selectRoleIdByUserId(userId);
        //排序时为了减少比较,roleIdListIdDb已经排序过了
        Collections.sort(newRoleIdList);
        //角色没变化，直接退出
        if (Objects.equals(newRoleIdList, roleIdListIdDb)) {
            return;
        }
        //取差集为新增的
        Collection<Long> addRoleIdList = CollUtil.subtract(newRoleIdList, roleIdListIdDb);
        if (!addRoleIdList.isEmpty()) {
            List<SysUserRole> addUrList = CollUtils.toList(vo.getRoleIdList(), roleId -> new SysUserRole(userId, roleId));
            sysUserRoleMapper.saveAllAndFlush(addUrList);
        }
        //取差集为删除的
        Collection<Long> deleteRoleIdList = CollUtil.subtract(roleIdListIdDb, newRoleIdList);
        if (!deleteRoleIdList.isEmpty()) {
            sysUserRoleMapper.deleteByUserIdAndRoleIdIn(userId, deleteRoleIdList);
        }
        rbacCache.userClear(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long userId) {
        //用户表删除
        sysUserMapper.deleteById(userId);
        //用户角色表删除
        sysUserRoleMapper.deleteByUserIdAndRoleIdIn(userId, null);
        rbacCache.userClear(userId);
    }

    /**
     * 获取当前用户的动态路由树
     *
     * @return 菜单树
     */
    @Override
    @Transactional(readOnly = true)
    public SysUserInfo userInfo() {
        SysUser user = sysUserMapper.findByUsername(LoginUser.username());
        List<SysMenu> menus = rbacCache.userTree(LoginUser.userId());
        return new SysUserInfo(user, menus);
    }

    /**
     * @param vo 密码更新请求
     */
    @Override
    public void updatePasswd(PasswdUpdateVO vo) {
        //校验新旧密码是否一致，一致则不修改
        RbacResultEnum.PASSWD_NOT_CHANGE.isFalse(Objects.equals(vo.oldPasswd(), vo.newPasswd()));
        //校验用户旧密码是否正确
        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(LoginUser.username(), vo.oldPasswd());
        authenticationManager.authenticate(authenticationToken);
        //修改密码
        Long userId = LoginUser.userId();
        SysUser user = sysUserMapper.findById(userId).orElse(null);
        user.setPassword(PasswordFactory.encode(vo.newPasswd()));
        sysUserMapper.save(user);
        //清除用户token，让用户重新登录
        tokenService.del(userId);
    }

    /**
     * 重置密码
     *
     * @param userId 用户id
     */
    @Override
    public void resetPasswd(Long userId) {
        SysUser user = sysUserMapper.findById(userId).orElse(null);
        user.setPassword(PasswordFactory.reset(user.getUsername()));
        sysUserMapper.save(user);
        tokenService.del(userId);
    }

    /**
     * 禁用用户
     *
     * @param userId 用户id
     */
    @Override
    public void ban(Long userId) {
        //不能封禁/解封自己
        RbacResultEnum.CAN_NOT_BAN_MYSELF.notEquals(userId, LoginUser.userId());
        SysUser user = sysUserMapper.findById(userId).orElse(null);
        //状态取反即可
        user.setStatus(!user.getStatus());
        sysUserMapper.save(user);
        //清除token
        tokenService.del(userId);
    }

}

