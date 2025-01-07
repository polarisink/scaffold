package com.scaffold.biz.module.rbac.components;


import com.scaffold.biz.module.rbac.entity.SysMenu;
import com.scaffold.biz.module.rbac.contant.RbacCacheConst;
import com.scaffold.biz.module.rbac.mapper.SysMenuMapper;
import com.scaffold.biz.module.rbac.vo.menu.SysRoleWrapper;
import com.scaffold.core.base.constant.GlobalConstant;
import com.scaffold.core.base.util.CollUtils;
import com.scaffold.core.base.util.ITree;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static com.scaffold.biz.module.rbac.contant.RbacCacheConst.USER_TREE;


/**
 * 菜单缓存管理器，包括用户和角色的菜单树结构
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RbacCache {
    private final SysMenuMapper sysMenuMapper;


    /**
     * 用户菜单缓存
     *
     * @param userId 用户id
     * @return 用户菜单树
     */
    @Cacheable(cacheNames = USER_TREE, key = "#userId")
    public List<SysMenu> userTree(Long userId) {
        List<SysMenu> menuSet = sysMenuMapper.findMenuCollByUserId(userId);
        List<SysMenu> tree = ITree.toTree(GlobalConstant.ROOT_PARENT_ID, menuSet, Comparator.comparing(SysMenu::getSortNo));
        log.info("cache user tree success,key: 【{}】", userId);
        return tree;
    }

    /**
     * 通过角色id获取对应菜单树
     *
     * @param roleId 角色id
     * @return 菜单树
     */
    @Cacheable(cacheNames = RbacCacheConst.ROLE_TREE, key = "#roleId")
    public SysRoleWrapper roleWrapper(Long roleId) {
        List<SysMenu> menuSet = sysMenuMapper.findMenuCollByRoleId(roleId);
        List<SysMenu> tree = ITree.toTree(GlobalConstant.ROOT_PARENT_ID, menuSet, Comparator.comparing(SysMenu::getSortNo));
        Set<Long> pidList = sysMenuMapper.selectAllParentId();
        //过滤掉上级节点，只要子节点，为了适配前端组件
        List<Long> list = CollUtils.toList(menuSet, m -> !pidList.contains(m.getId()), SysMenu::getId);
        log.info("cache role tree success,key: 【{}】", roleId);
        return new SysRoleWrapper(tree, list);
    }

    /**
     * 菜单树缓存
     *
     * @return 树
     */
    @Cacheable(cacheNames = RbacCacheConst.MENU_TREE, key = "0")
    public List<SysMenu> menuTree() {
        List<SysMenu> menuList = sysMenuMapper.findAll();
        List<SysMenu> tree = ITree.toTree(GlobalConstant.ROOT_PARENT_ID, menuList, Comparator.comparing(SysMenu::getSortNo));
        log.info("cache menu tree success");
        return tree;
    }


    /**
     * 通过用户id清除缓存
     *
     * @param userId 用户id
     */
    @CacheEvict(cacheNames = USER_TREE, key = "#userId")
    public void userClear(Long userId) {
        log.info("clear user tree cache success: 【{}】", userId);
    }

    /**
     * 通过角色id清除缓存
     *
     * @param roleId 角色id
     */
    @CacheEvict(cacheNames = RbacCacheConst.ROLE_TREE, key = "#roleId")
    public void roleClear(Long roleId) {
        log.info("clear role tree cache success: 【{}】", roleId);
    }
}
