package com.scaffold.rbac.service;

import cn.hutool.core.util.ObjectUtil;
import com.scaffold.rbac.components.RbacCache;
import com.scaffold.rbac.contant.RbacResultEnum;
import com.scaffold.rbac.entity.SysMenu;
import com.scaffold.rbac.mapper.SysMenuMapper;
import com.scaffold.rbac.mapper.SysRoleMenuMapper;
import com.scaffold.rbac.mapper.SysUserRoleMapper;
import com.scaffold.rbac.vo.menu.SysMenuCreateVO;
import com.scaffold.rbac.vo.menu.SysMenuUpdateVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.scaffold.rbac.contant.RbacCacheConst.MENU_TREE;

@Service
@RequiredArgsConstructor
public class SysMenuService {

    private final SysMenuMapper sysMenuMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final RbacCache rbacCache;
    private final SysUserRoleMapper sysUserRoleMapper;

    public List<SysMenu> tree() {
        return rbacCache.menuTree();
    }

    @CacheEvict(cacheNames = MENU_TREE, key = "0")
    public String save(SysMenuCreateVO vo) {
        RbacResultEnum.UNIQUE_MENU_NAME.isFalse(sysMenuMapper.existsByMenuName(vo.getMenuName()));
        RbacResultEnum.MENU_URL_NOT_FOUND.isFalse(ObjectUtil.equals(1, vo.getMenuType()) && (vo.getMenuUrl() == null || vo.getMenuUrl().isBlank()));
        if (vo.getSortNo() == null) {
            long count = sysMenuMapper.countByParentId(vo.getParentId());
            vo.setSortNo((count == 0) ? 0 : (int) (count + 10));
        }

        SysMenu entity = new SysMenu();
        BeanUtils.copyProperties(vo, entity);
        sysMenuMapper.insert(entity);
        return entity.getId().toString();
    }

    @CacheEvict(cacheNames = MENU_TREE, key = "0")
    public void updateById(SysMenuUpdateVO vo) {
        SysMenu entity = sysMenuMapper.selectById(vo.getId());
        boolean sameOrUnique = Objects.equals(vo.getMenuName(), entity.getMenuName()) || !sysMenuMapper.existsByMenuName(vo.getMenuName());
        RbacResultEnum.UNIQUE_MENU_NAME.isTrue(sameOrUnique);
        BeanUtils.copyProperties(vo, entity);
        sysMenuMapper.updateById(entity);
        clearUserAndRoleCache(vo.getId());
    }

    @CacheEvict(cacheNames = MENU_TREE, key = "0")
    public void deleteById(Long menuId) {
        RbacResultEnum.CAN_NOT_DELETE_PARENT_MENU_NODE.isFalse(sysMenuMapper.existsByParentId(menuId));
        sysMenuMapper.deleteById(menuId);
        clearUserAndRoleCache(menuId);
    }

    private void clearUserAndRoleCache(Long menuId) {
        List<Long> roleIdList = sysRoleMenuMapper.selectRoleIdCollByMenuId(menuId);
        if (!roleIdList.isEmpty()) {
            roleIdList.forEach(rbacCache::roleClear);
            sysUserRoleMapper.selectUserIdByRoleIdIn(roleIdList).forEach(rbacCache::userClear);
        }
    }
}
