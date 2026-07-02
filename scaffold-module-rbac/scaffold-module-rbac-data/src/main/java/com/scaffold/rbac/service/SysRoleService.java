package com.scaffold.rbac.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mzt.logapi.starter.annotation.LogRecord;
import com.scaffold.base.constant.GlobalConstant;
import com.scaffold.base.util.CollUtils;
import com.scaffold.base.util.PageResponse;
import com.scaffold.base.util.TreeIterators;
import com.scaffold.rbac.components.RbacCache;
import com.scaffold.rbac.contant.RbacResultEnum;
import com.scaffold.rbac.entity.SysRole;
import com.scaffold.rbac.entity.SysRoleMenu;
import com.scaffold.rbac.mapper.SysMenuMapper;
import com.scaffold.rbac.mapper.SysRoleMapper;
import com.scaffold.rbac.mapper.SysRoleMenuMapper;
import com.scaffold.rbac.mapper.SysUserRoleMapper;
import com.scaffold.rbac.vo.menu.SysRoleWrapper;
import com.scaffold.rbac.vo.role.SysRoleCreateVO;
import com.scaffold.rbac.vo.role.SysRolePageVO;
import com.scaffold.rbac.vo.role.SysRoleUpdateVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final RbacCache rbacCache;
    private final SysMenuMapper sysMenuMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    public PageResponse<SysRole> page(SysRolePageVO vo) {
        IPage<SysRole> page = sysRoleMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()),
                Wrappers.<SysRole>lambdaQuery()
                        .like(StrUtil.isNotBlank(vo.getRoleName()), SysRole::getRoleName, vo.getRoleName())
                        .like(StrUtil.isNotBlank(vo.getRoleCode()), SysRole::getRoleCode, vo.getRoleCode())
                        .orderByDesc(SysRole::getGmtModified));
        return new PageResponse<>(page.getRecords(), page.getPages(), page.getCurrent(), page.getTotal(), page.getSize());
    }

    @Transactional(readOnly = true)
    public SysRoleWrapper roleWrapper(Long roleId) {
        return rbacCache.roleWrapper(roleId);
    }

    // @formatter:off
    @LogRecord(type = "角色模块",
            subType = "新增角色",
            success = "新增角色【{{#vo.roleName}}】，菜单ID：{{#_ret}}",
            fail = "新增角色【{{#vo.roleName}}】失败，原因：{{#_errorMsg}}",
            bizNo = "{{#_ret}}",
            extra = "{{#vo.toString()}}"
    )
    // @formatter:on
    @Transactional(rollbackFor = Exception.class)
    public Long save(SysRoleCreateVO vo) {
        RbacResultEnum.UNIQUE_ROLE_NAME.isFalse(sysRoleMapper.existsByRoleName(vo.roleName()));
        RbacResultEnum.UNIQUE_ROLE_CODE.isFalse(sysRoleMapper.existsByRoleCode(vo.roleCode()));

        SysRole entity = new SysRole();
        BeanUtils.copyProperties(vo, entity);
        sysRoleMapper.insert(entity);
        Long roleId = entity.getId();
        if (vo.menuIdList() == null || vo.menuIdList().isEmpty()) {
            return roleId;
        }
        Set<Long> allMenuIdSet = foreFathersIdList(vo.menuIdList());
        List<SysRoleMenu> roleMenuList = CollUtils.toList(allMenuIdSet, menuId -> new SysRoleMenu(roleId, menuId));
        sysRoleMenuMapper.insertBatchSomeColumn(roleMenuList);
        return roleId;
    }

    private Set<Long> foreFathersIdList(Collection<Long> menuIdSet) {
        if (menuIdSet == null || menuIdSet.isEmpty()) {
            return Set.of();
        }
        Set<Long> res = TreeIterators.iterate(true, menuIdSet, sysMenuMapper::findParentIdByIdIn);
        res.remove(GlobalConstant.ROOT_PARENT_ID);
        return res;
    }

    // @formatter:off
    @LogRecord(
            success = "更新角色【{{#vo.roleName}}】，菜单ID：{{#vo.id}}",
            type = "角色模块",
            subType = "更新角色",
            bizNo = "{{#vo.id}}",
            fail = "更新角色【{{#vo.roleName}}】失败，原因：{{#_errorMsg}}"
    )
    // @formatter:on
    @Transactional(rollbackFor = Exception.class)
    public void updateById(SysRoleUpdateVO vo) {
        Long roleId = vo.id();
        SysRole entity = sysRoleMapper.selectById(roleId);
        boolean uniqueName = Objects.equals(vo.roleName(), entity.getRoleName()) || !sysRoleMapper.existsByRoleName(vo.roleName());
        RbacResultEnum.UNIQUE_ROLE_NAME.isTrue(uniqueName);
        boolean uniqueCode = Objects.equals(vo.roleCode(), entity.getRoleCode()) || !sysRoleMapper.existsByRoleCode(vo.roleCode());
        RbacResultEnum.UNIQUE_ROLE_CODE.isTrue(uniqueCode);
        BeanUtils.copyProperties(vo, entity);
        sysRoleMapper.updateById(entity);
        List<Long> newMenuIdList = new ArrayList<>(foreFathersIdList(vo.menuIdList()));
        List<Long> menuIdInDb = sysRoleMenuMapper.selectMenuIdByRoleId(roleId);
        List<Long> affectedUserIdList = sysUserRoleMapper.selectUserIdByRoleIdIn(List.of(roleId));
        if (Objects.equals(newMenuIdList, menuIdInDb)) {
            affectedUserIdList.forEach(rbacCache::userClear);
            return;
        }
        Collection<Long> addList = CollUtil.subtract(newMenuIdList, menuIdInDb);
        if (!addList.isEmpty()) {
            List<SysRoleMenu> addRoleMenuList = CollUtils.toList(addList, menuId -> new SysRoleMenu(roleId, menuId));
            sysRoleMenuMapper.insertBatchSomeColumn(addRoleMenuList);
        }
        Collection<Long> deleteList = CollUtil.subtract(menuIdInDb, newMenuIdList);
        if (!deleteList.isEmpty()) {
            sysRoleMenuMapper.deleteByRoleIdAndMenuIdIn(roleId, deleteList);
        }
        rbacCache.roleClear(roleId);
        affectedUserIdList.forEach(rbacCache::userClear);
    }

    // @formatter:off
    @LogRecord(
            success = "删除角色【{{#roleId}}】，菜单ID：{{#vo.id}}",
            type = "角色模块",
            subType = "删除角色",
            bizNo = "{{#id}}",
            fail = "更新角色【{{#roleId}}】失败，原因：{{#_errorMsg}}"
    )
    // @formatter:on
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long roleId) {
        RbacResultEnum.CAN_NOT_DELETE_ROLE_HAS_USER.isFalse(sysUserRoleMapper.existsByRoleId(roleId));
        sysRoleMapper.deleteById(roleId);
        sysRoleMenuMapper.deleteByRoleIdAndMenuIdIn(roleId, null);
        rbacCache.roleClear(roleId);
    }
}
