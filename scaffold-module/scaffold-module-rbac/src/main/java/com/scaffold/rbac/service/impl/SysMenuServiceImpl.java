package com.scaffold.rbac.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.scaffold.rbac.components.RbacCache;
import com.scaffold.rbac.contant.RbacResultEnum;
import com.scaffold.rbac.entity.SysMenu;
import com.scaffold.rbac.mapper.SysMenuMapper;
import com.scaffold.rbac.mapper.SysRoleMenuMapper;
import com.scaffold.rbac.mapper.SysUserRoleMapper;
import com.scaffold.rbac.service.SysMenuService;
import com.scaffold.rbac.vo.menu.SysMenuCreateVO;
import com.scaffold.rbac.vo.menu.SysMenuUpdateVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.scaffold.rbac.contant.RbacCacheConst.MENU_TREE;

/**
 * 菜单(SysMenu)表服务实现类
 *
 * @author aries
 * @since 2024-07-22 20:38:40
 */
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl implements SysMenuService {

    private final SysMenuMapper sysMenuMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final RbacCache rbacCache;
    private final SysUserRoleMapper sysUserRoleMapper;

    /**
     * 菜单树
     *
     * @return 菜单树
     */
    @Override
    public List<SysMenu> tree() {
        return rbacCache.menuTree();
    }

    @Override
    @CacheEvict(cacheNames = MENU_TREE, key = "0")
    public String save(SysMenuCreateVO vo) {
        //是否是同级不能重名
        RbacResultEnum.UNIQUE_MENU_NAME.isFalse(sysMenuMapper.existsByMenuName(vo.getMenuName()));
        //当类型为菜单时地址不可为空
        RbacResultEnum.MENU_URL_NOT_FOUND.isFalse(ObjectUtil.equals(1, vo.getMenuType()) && (vo.getMenuUrl() == null || vo.getMenuUrl().isBlank()));
        //判断是否输入排序字段,未输入时将通过代码设置排序
        if (vo.getSortNo() == null) {
            //判断当前父类下的数据数量
            long count = sysMenuMapper.countByParentId(vo.getParentId());
            //设置排序
            vo.setSortNo((count == 0) ? 0 : (int) (count + 10));
        }

        SysMenu entity = new SysMenu();
        BeanUtils.copyProperties(vo, entity);
        sysMenuMapper.saveAndFlush(entity);
        return entity.getId().toString();
    }

    @Override
    @CacheEvict(cacheNames = MENU_TREE, key = "0")
    public void updateById(SysMenuUpdateVO vo) {
        SysMenu entity = sysMenuMapper.findById(vo.getId()).orElse(null);
        boolean sameOrUnique = Objects.equals(vo.getMenuName(), entity.getMenuName()) || !sysMenuMapper.existsByMenuName(vo.getMenuName());
        //名字要么没改，要么与数据库的不能重复
        RbacResultEnum.UNIQUE_MENU_NAME.isTrue(sameOrUnique);
        BeanUtils.copyProperties(vo, entity);
        sysMenuMapper.save(entity);
        //清除菜单缓存
        clearUserAndRoleCache(vo.getId());
    }

    @Override
    @CacheEvict(cacheNames = MENU_TREE, key = "0")
    public void deleteById(Long menuId) {
        //不能删除有子菜单的
        RbacResultEnum.CAN_NOT_DELETE_PARENT_MENU_NODE.isFalse(sysMenuMapper.existsByParentId(menuId));
        sysMenuMapper.deleteById(menuId);
        clearUserAndRoleCache(menuId);
    }

    /**
     * 通过菜单id删除菜单、角色、用户的菜单缓存
     *
     * @param menuId 菜单id
     */
    private void clearUserAndRoleCache(Long menuId) {
        //清除菜单缓存
        List<Long> roleIdList = sysRoleMenuMapper.selectRoleIdCollByMenuId(menuId);
        if (!roleIdList.isEmpty()) {
            //有角色就清除角色缓存
            roleIdList.forEach(rbacCache::roleClear);
            //通过角色查用户，有用户就清理缓存
            sysUserRoleMapper.selectUserIdByRoleIdIn(roleIdList).forEach(rbacCache::userClear);
        }
    }

}

