package com.scaffold.bootstrap;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scaffold.base.constant.GlobalConstant;
import com.scaffold.rbac.contant.RbacCacheConst;
import com.scaffold.rbac.entity.*;
import com.scaffold.rbac.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 为 scaffold-biz 初始化可以直接登录和访问管理后台的最小 RBAC 数据。
 * 所有数据均按业务唯一键检查，可以安全地重复启动应用。
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class RbacDataInitializer implements ApplicationRunner {

    private static final String ADMIN = "admin";
    private static final String DEFAULT_ORG_ID = "0";

    private final SysMenuMapper menuMapper;
    private final SysRoleMapper roleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final CacheManager cacheManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(ApplicationArguments args) {
        Map<String, Long> menuIds = initializeMenus();
        SysRole adminRole = initializeAdminRole();
        List<Long> allMenuIds = menuMapper.selectList(null).stream().map(SysMenu::getId).toList();
        grantAllMenus(adminRole.getId(), allMenuIds);
        SysUser adminUser = initializeAdminUser();
        grantAdminRole(adminUser.getId(), adminRole.getId());
        clearRbacCaches();
        log.info("RBAC bootstrap data ready: user=admin, role=admin, menus={}", menuIds.size());
    }

    private Map<String, Long> initializeMenus() {
        Map<String, Long> ids = new LinkedHashMap<>();
        ids.put("dashboard", ensureMenu(new MenuSeed(
                "仪表盘", "/dashboard", 0, null, "lucide:layout-dashboard", -1, GlobalConstant.ROOT_PARENT_ID)));
        ids.put("analytics", ensureMenu(new MenuSeed(
                "分析页", "/dashboard/analytics", 1, "dashboard:analytics:view", "lucide:area-chart", 0, ids.get("dashboard"))));
        ids.put("workspace", ensureMenu(new MenuSeed(
                "工作台", "/dashboard/workspace", 1, "dashboard:workspace:view", "carbon:workspace", 10, ids.get("dashboard"))));

        ids.put("system", ensureMenu(new MenuSeed(
                "系统管理", "/system", 0, null, "lucide:settings-2", 10, GlobalConstant.ROOT_PARENT_ID)));
        ids.put("user", ensureMenu(new MenuSeed(
                "用户管理", "/system/user", 1, "system:user:view", "lucide:users", 0, ids.get("system"))));
        ids.put("role", ensureMenu(new MenuSeed(
                "角色管理", "/system/role", 1, "system:role:view", "lucide:badge-check", 10, ids.get("system"))));
        ids.put("menu", ensureMenu(new MenuSeed(
                "菜单管理", "/system/menu", 1, "system:menu:view", "lucide:list-tree", 20, ids.get("system"))));
        return ids;
    }

    private Long ensureMenu(MenuSeed seed) {
        SysMenu existing = menuMapper.selectOne(Wrappers.<SysMenu>lambdaQuery()
                .eq(SysMenu::getPath, seed.path())
                .last("limit 1"));
        if (existing != null) {
            return existing.getId();
        }
        SysMenu menu = new SysMenu();
        menu.setMenuName(seed.name());
        menu.setPath(seed.path());
        menu.setMenuType(seed.type());
        menu.setMenuUrl(seed.permission());
        menu.setMenuIconUrl(seed.icon());
        menu.setSortNo(seed.sort());
        menu.setParentId(seed.parentId());
        menuMapper.insert(menu);
        return menu.getId();
    }

    private SysRole initializeAdminRole() {
        SysRole role = roleMapper.selectOne(Wrappers.<SysRole>lambdaQuery()
                .eq(SysRole::getRoleCode, ADMIN)
                .last("limit 1"));
        if (role != null) {
            return role;
        }
        role = new SysRole("管理员", ADMIN);
        role.setDescription("拥有全部后台路由权限的系统管理员");
        roleMapper.insert(role);
        return role;
    }

    private void grantAllMenus(Long roleId, List<Long> menuIds) {
        Set<Long> grantedIds = Set.copyOf(roleMenuMapper.selectMenuIdByRoleId(roleId));
        List<SysRoleMenu> missingRelations = menuIds.stream()
                .filter(menuId -> !grantedIds.contains(menuId))
                .map(menuId -> new SysRoleMenu(roleId, menuId))
                .toList();
        if (!missingRelations.isEmpty()) {
            roleMenuMapper.insertBatchSomeColumn(missingRelations);
        }
    }

    private SysUser initializeAdminUser() {
        SysUser user = userMapper.findByUsername(ADMIN);
        if (user != null) {
            return user;
        }
        user = new SysUser();
        user.setUsername(ADMIN);
        user.setPassword(passwordEncoder.encode(ADMIN));
        user.setOrgId(DEFAULT_ORG_ID);
        user.setStatus(true);
        userMapper.insert(user);
        return user;
    }

    private void grantAdminRole(Long userId, Long roleId) {
        if (!userRoleMapper.selectRoleIdByUserId(userId).contains(roleId)) {
            userRoleMapper.insert(new SysUserRole(userId, roleId));
        }
    }

    private void clearRbacCaches() {
        for (String cacheName : List.of(
                RbacCacheConst.MENU_TREE,
                RbacCacheConst.ROLE_TREE,
                RbacCacheConst.USER_TREE,
                RbacCacheConst.USER_ROLES,
                RbacCacheConst.USER_PERMISSIONS)) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        }
    }

    private record MenuSeed(
            String name,
            String path,
            Integer type,
            String permission,
            String icon,
            Integer sort,
            Long parentId) {
    }
}
