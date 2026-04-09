package com.scaffold.rbac.service;

import cn.hutool.core.collection.CollUtil;
import com.scaffold.base.constant.GlobalConstant;
import com.scaffold.base.util.CollUtils;
import com.scaffold.base.util.ITree;
import com.scaffold.base.util.PageResponse;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
        return sysRoleMapper.page(vo);
    }

    @Transactional(readOnly = true)
    public SysRoleWrapper roleWrapper(Long roleId) {
        return rbacCache.roleWrapper(roleId);
    }

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
        Set<Long> res = ITree.iterate(true, menuIdSet, sysMenuMapper::findParentIdByIdIn);
        res.remove(GlobalConstant.ROOT_PARENT_ID);
        return res;
    }

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
        if (Objects.equals(newMenuIdList, menuIdInDb)) {
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
        sysUserRoleMapper.selectUserIdByRoleIdIn(List.of(roleId)).forEach(rbacCache::userClear);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long roleId) {
        RbacResultEnum.CAN_NOT_DELETE_ROLE_HAS_USER.isFalse(sysUserRoleMapper.existsByRoleId(roleId));
        sysRoleMapper.deleteById(roleId);
        sysRoleMenuMapper.deleteByRoleIdAndMenuIdIn(roleId, null);
        rbacCache.roleClear(roleId);
    }
}
