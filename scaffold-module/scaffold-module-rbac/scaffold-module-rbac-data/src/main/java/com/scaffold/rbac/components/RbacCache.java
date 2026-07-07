package com.scaffold.rbac.components;


import com.scaffold.base.constant.GlobalConstant;
import com.scaffold.base.util.CollUtils;
import com.scaffold.base.util.ITree;
import com.scaffold.rbac.contant.RbacCacheConst;
import com.scaffold.rbac.entity.SysMenu;
import com.scaffold.rbac.entity.SysOrg;
import com.scaffold.rbac.entity.SysRole;
import com.scaffold.rbac.mapper.SysMenuMapper;
import com.scaffold.rbac.mapper.SysOrgMapper;
import com.scaffold.rbac.mapper.SysRoleMapper;
import com.scaffold.rbac.mapper.SysUserRoleMapper;
import com.scaffold.rbac.vo.menu.SysRoleWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static com.scaffold.base.constant.GlobalConstant.ROOT_PARENT_ID_STR;
import static com.scaffold.rbac.contant.RbacCacheConst.*;


/**
 * 菜单缓存管理器，包括用户和角色的菜单树结构
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RbacCache {
    private final SysMenuMapper sysMenuMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysOrgMapper sysOrgMapper;

    /**
     * 用户菜单缓存
     *
     * @param userId 用户id
     * @return 用户菜单树
     */
    @Cacheable(cacheNames = USER_TREE_CACHE, key = "#userId")
    public List<SysMenu> userTree(Long userId) {
        List<SysMenu> menuSet = sysMenuMapper.findMenuCollByUserId(userId);
        List<SysMenu> tree = ITree.toTree(GlobalConstant.ROOT_PARENT_ID, menuSet, Comparator.comparing(SysMenu::getSortNo));
        log.info("cache user tree success,key: 【{}】", userId);
        return tree;
    }

    public List<SysRole> roles(Long userId) {
        List<Long> roleIds = sysUserRoleMapper.selectRoleIdByUserId(userId);
        return sysRoleMapper.selectByIds(roleIds);
    }

    /**
     * 通过角色id获取对应菜单树
     *
     * @param roleId 角色id
     * @return 菜单树
     */
    @Cacheable(cacheNames = RbacCacheConst.ROLE_TREE_CACHE, key = "#roleId")
    public SysRoleWrapper roleWrapper(Long roleId) {
        List<SysMenu> menuSet = sysMenuMapper.findMenuCollByRoleId(roleId);
        List<SysMenu> tree = ITree.toTree(GlobalConstant.ROOT_PARENT_ID, menuSet, Comparator.comparing(SysMenu::getSortNo));
        Set<Long> pidList = sysMenuMapper.selectAllParentId();
        // 过滤掉上级节点，只要子节点，为了适配前端组件
        List<Long> list = CollUtils.toList(menuSet, m -> !pidList.contains(m.getId()), SysMenu::getId);
        log.info("cache role tree success,key: 【{}】", roleId);
        return new SysRoleWrapper(tree, list);
    }

    /**
     * 菜单树缓存
     *
     * @return 树
     */
    @Cacheable(cacheNames = RbacCacheConst.MENU_TREE_CACHE, key = ROOT_PARENT_ID_STR)
    public List<SysMenu> menuTree() {
        List<SysMenu> menuList = sysMenuMapper.selectList(null);
        List<SysMenu> tree = ITree.toTree(GlobalConstant.ROOT_PARENT_ID, menuList, Comparator.comparing(SysMenu::getSortNo));
        log.info("cache menu tree success");
        return tree;
    }

    @Cacheable(cacheNames = RbacCacheConst.ORG_TREE_CACHE, key = ROOT_PARENT_ID_STR)
    public List<SysOrg> orgTree() {
        List<SysOrg> orgList = sysOrgMapper.selectList(null);
        Comparator<SysOrg> comparator = Comparator.comparing(SysOrg::getSort, Comparator.nullsLast(Integer::compareTo)).thenComparing(SysOrg::getId);
        List<SysOrg> tree = ITree.toTree(GlobalConstant.ROOT_PARENT_ID, orgList, comparator);
        log.info("cache org tree success");
        return tree;
    }

    @CacheEvict(cacheNames = RbacCacheConst.ORG_TREE_CACHE, key = ROOT_PARENT_ID_STR)
    public void orgClear() {
        log.info("clear org tree cache success");
    }


    /**
     * 通过用户id清除缓存
     *
     * @param userId 用户id
     */
    @Caching(evict = {
            @CacheEvict(cacheNames = USER_TREE_CACHE, key = "#userId"),
            @CacheEvict(cacheNames = USER_ROLES_CACHE, key = "#userId"),
            @CacheEvict(cacheNames = USER_PERMISSIONS_CACHE, key = "#userId")
    })
    public void userClear(Long userId) {
        log.info("clear user tree cache success: 【{}】", userId);
    }

    /**
     * 通过角色id清除缓存
     *
     * @param roleId 角色id
     */
    @CacheEvict(cacheNames = RbacCacheConst.ROLE_TREE_CACHE, key = "#roleId")
    public void roleClear(Long roleId) {
        log.info("clear role tree cache success: 【{}】", roleId);
    }
}
