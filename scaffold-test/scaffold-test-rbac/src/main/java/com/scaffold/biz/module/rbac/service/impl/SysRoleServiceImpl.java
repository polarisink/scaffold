package com.scaffold.biz.module.rbac.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.scaffold.biz.module.rbac.components.RbacCache;
import com.scaffold.biz.module.rbac.contant.RbacResultEnum;
import com.scaffold.biz.module.rbac.entity.SysRole;
import com.scaffold.biz.module.rbac.entity.SysRoleMenu;
import com.scaffold.biz.module.rbac.mapper.SysMenuMapper;
import com.scaffold.biz.module.rbac.mapper.SysRoleMapper;
import com.scaffold.biz.module.rbac.mapper.SysRoleMenuMapper;
import com.scaffold.biz.module.rbac.mapper.SysUserRoleMapper;
import com.scaffold.biz.module.rbac.service.SysRoleService;
import com.scaffold.biz.module.rbac.vo.menu.SysRoleWrapper;
import com.scaffold.biz.module.rbac.vo.role.SysRoleConvert;
import com.scaffold.biz.module.rbac.vo.role.SysRoleCreateVO;
import com.scaffold.biz.module.rbac.vo.role.SysRolePageVO;
import com.scaffold.biz.module.rbac.vo.role.SysRoleUpdateVO;
import com.scaffold.core.base.constant.GlobalConstant;
import com.scaffold.core.base.util.CollUtils;
import com.scaffold.core.base.util.ITree;
import com.scaffold.core.base.util.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 角色(SysRole)表服务实现类
 *
 * @author aries
 * @since 2024-07-22 20:38:40
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final RbacCache rbacCache;
    private final SysMenuMapper sysMenuMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    @Override
    public PageResponse<SysRole> page(SysRolePageVO vo) {
        return sysRoleMapper.page(vo);
    }

    @Override
    @Transactional(readOnly = true)
    public SysRoleWrapper roleWrapper(Long roleId) {
        return rbacCache.roleWrapper(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(SysRoleCreateVO vo) {
        SysRole entity = SysRoleConvert.INSTANCE.convert(vo);
        //名字不能重复
        RbacResultEnum.UNIQUE_ROLE_NAME.isFalse(sysRoleMapper.existsByRoleName(vo.roleName()));
        //编码不能重复
        RbacResultEnum.UNIQUE_ROLE_CODE.isFalse(sysRoleMapper.existsByRoleCode(vo.roleCode()));
        sysRoleMapper.saveAndFlush(entity);
        Long roleId = entity.getId();
        //参训人员没有后台管理的权限，因此这里可以为空
        if (vo.menuIdList() == null || vo.menuIdList().isEmpty()) {
            return roleId;
        }
        //保存角色菜单关联
        //这里因为前端组件只能传输树的叶子节点，因此需要后端迭代找到所有祖先id集合，再做保存
        Set<Long> allMenuIdSet = foreFathersIdList(vo.menuIdList());
        List<SysRoleMenu> roleMenuList = CollUtils.toList(allMenuIdSet, menuId -> new SysRoleMenu(roleId, menuId));
        sysRoleMenuMapper.saveAllAndFlush(roleMenuList);
        //加入缓存
        //menuCache.roleClear(roleId);
        return roleId;
    }

    /**
     * 通过菜单叶子节点找到所有上级节点
     *
     * @param menuIdSet 菜单id set
     * @return 所有菜单id set
     */
    private Set<Long> foreFathersIdList(Collection<Long> menuIdSet) {
        if (menuIdSet == null || menuIdSet.isEmpty()) {
            return Set.of();
        }
        Set<Long> res = ITree.iterate(true, menuIdSet, sysMenuMapper::findParentIdByIdIn);
        res.remove(GlobalConstant.ROOT_PARENT_ID);
        return res;
    }

    /**
     * 修改之后修改用户树结构
     *
     * @param vo 实体对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateById(SysRoleUpdateVO vo) {
        Long roleId = vo.id();
        SysRole entity = sysRoleMapper.findById(roleId).orElse(null);
        //名字不能重复,角色编码不能重复
        boolean uniqueName = Objects.equals(vo.roleName(), entity.getRoleName()) || !sysRoleMapper.existsByRoleName(vo.roleName());
        RbacResultEnum.UNIQUE_ROLE_NAME.isTrue(uniqueName);
        boolean uniqueCode = Objects.equals(vo.roleCode(), entity.getRoleCode()) || !sysRoleMapper.existsByRoleCode(vo.roleCode());
        RbacResultEnum.UNIQUE_ROLE_CODE.isTrue(uniqueCode);
        BeanUtils.copyProperties(vo, entity);
        sysRoleMapper.save(entity);
        List<Long> newMenuIdList = new ArrayList<>(foreFathersIdList(vo.menuIdList()));
        List<Long> menuIdInDb = sysRoleMenuMapper.selectMenuIdByRoleId(roleId);
        //没变化直接退出
        if (Objects.equals(newMenuIdList, menuIdInDb)) {
            return;
        }
        Collection<Long> addList = CollUtil.subtract(newMenuIdList, menuIdInDb);
        if (!addList.isEmpty()) {
            //有新增的就增加
            List<SysRoleMenu> addRoleMenuList = CollUtils.toList(addList, menuId -> new SysRoleMenu(roleId, menuId));
            sysRoleMenuMapper.saveAllAndFlush(addRoleMenuList);
        }
        Collection<Long> deleteList = CollUtil.subtract(menuIdInDb, newMenuIdList);
        if (!deleteList.isEmpty()) {
            sysRoleMenuMapper.deleteByRoleIdAndMenuIdIn(roleId, deleteList);
        }
        //清除角色菜单缓存
        rbacCache.roleClear(roleId);
        //清除有该角色用户的菜单缓存
        sysUserRoleMapper.selectUserIdByRoleIdIn(List.of(roleId)).forEach(rbacCache::userClear);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long roleId) {
        //有用户的角色不能删
        RbacResultEnum.CAN_NOT_DELETE_ROLE_HAS_USER.isFalse(sysUserRoleMapper.existsByRoleId(roleId));
        //删除角色
        sysRoleMapper.deleteById(roleId);
        //删除角色菜单关联
        sysRoleMenuMapper.deleteByRoleIdAndMenuIdIn(roleId, null);
        //删除角色菜单树
        rbacCache.roleClear(roleId);
    }
}

