package com.scaffold.rbac.components;

import com.scaffold.base.util.JsonUtil;
import com.scaffold.rbac.entity.*;
import com.scaffold.rbac.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RbacDataInitializer implements ApplicationRunner {

    private static final String SEED_DATA_PATH = "rbac/rbac-seed-data.json";

    /**
     * 启动时从 resources 读取最小 RBAC 数据集。
     * <p>
     * 初始化逻辑以业务唯一键判断是否已存在，避免重复启动时插入重复数据。
     */
    private final SysOrgMapper sysOrgMapper;
    private final SysMenuMapper sysMenuMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(ApplicationArguments args) throws IOException {
        RbacSeedData seedData = loadSeedData();
        // 顺序不可随意调整：菜单依赖父菜单，用户依赖组织，授权关系依赖用户/角色/菜单。
        SysOrg headquarters = initHeadquarters(seedData.org());
        initMenus(seedData.menus());
        SysRole adminRole = initAdminRole(seedData.adminRole());
        SysUser adminUser = initAdminUser(headquarters, seedData.adminUser());
        initAdminUserRole(adminUser, adminRole);
        initAdminRoleMenus(adminRole);
    }

    private RbacSeedData loadSeedData() throws IOException {
        ClassPathResource resource = new ClassPathResource(SEED_DATA_PATH);
        RbacSeedData seedData;
        try (InputStream inputStream = resource.getInputStream()) {
            seedData = JsonUtil.read(inputStream, RbacSeedData.class);
        }
        Objects.requireNonNull(seedData.org(), "RBAC init org seed data is required");
        Objects.requireNonNull(seedData.adminRole(), "RBAC init adminRole seed data is required");
        Objects.requireNonNull(seedData.adminUser(), "RBAC init adminUser seed data is required");
        Objects.requireNonNull(seedData.menus(), "RBAC init menus seed data is required");
        return seedData;
    }

    private SysOrg initHeadquarters(OrgSeed seed) {
        return sysOrgMapper.findByOrgCode(seed.orgCode()).orElseGet(() -> {
            SysOrg org = new SysOrg();
            org.setParentId(seed.parentId());
            org.setOrgName(seed.orgName());
            org.setOrgCode(seed.orgCode());
            org.setSort(seed.sort());
            sysOrgMapper.insert(org);
            return org;
        });
    }

    private void initMenus(List<MenuSeed> seeds) {
        // rbac-seed-data.json 中的 menus 已按父菜单在前、子菜单在后的顺序排列，便于直接解析 parentId。
        for (MenuSeed seed : seeds) {
            Long parentId = 0L;
            if (StringUtils.hasText(seed.parentPath())) {
                parentId = sysMenuMapper.findByPath(seed.parentPath())
                        .map(SysMenu::getId)
                        .orElseThrow(() -> new IllegalStateException("RBAC init menu parent not found: " + seed.parentPath()));
            }
            Long finalParentId = parentId;
            sysMenuMapper.findByPath(seed.path()).orElseGet(() -> {
                SysMenu menu = new SysMenu();
                menu.setParentId(finalParentId);
                menu.setMenuName(seed.menuName());
                menu.setPath(seed.path());
                menu.setMenuType(seed.menuType());
                menu.setMenuUrl(seed.menuUrl());
                menu.setMenuIconUrl(seed.menuIconUrl());
                menu.setSortNo(seed.sortNo());
                sysMenuMapper.insert(menu);
                return menu;
            });
        }
    }

    private SysRole initAdminRole(RoleSeed seed) {
        return sysRoleMapper.findByRoleCode(seed.roleCode()).orElseGet(() -> {
            SysRole role = new SysRole();
            role.setRoleName(seed.roleName());
            role.setRoleCode(seed.roleCode());
            role.setDescription(seed.description());
            sysRoleMapper.insert(role);
            return role;
        });
    }

    private SysUser initAdminUser(SysOrg headquarters, UserSeed seed) {
        // 只在首次创建默认管理员时写入初始密码，避免启动时覆盖人工修改后的密码。
        SysUser admin = sysUserMapper.findOptionalByUsername(seed.username()).orElseGet(() -> {
            SysUser user = new SysUser();
            user.setUsername(seed.username());
            user.setPassword(passwordEncoder.encode(seed.rawPassword()));
            user.setOrgId(headquarters.getId());
            user.setStatus(true);
            sysUserMapper.insert(user);
            return user;
        });
        // 历史数据可能缺少组织归属，这里仅补齐空组织，不覆盖人工调整过的组织。
        if (admin.getOrgId() == null || Objects.equals(admin.getOrgId(), 0L)) {
            admin.setOrgId(headquarters.getId());
            sysUserMapper.updateById(admin);
        }
        return admin;
    }

    private void initAdminUserRole(SysUser adminUser, SysRole adminRole) {
        if (!sysUserRoleMapper.existsByUserIdAndRoleId(adminUser.getId(), adminRole.getId())) {
            sysUserRoleMapper.insert(new SysUserRole(adminUser.getId(), adminRole.getId()));
        }
    }

    private void initAdminRoleMenus(SysRole adminRole) {
        // 管理员角色拥有所有有效菜单；后续新增种子菜单也会在下一次启动时补授权。
        Set<Long> existingMenuIds = sysRoleMenuMapper.selectMenuIdSetByRoleId(adminRole.getId());
        sysMenuMapper.selectAllMenuId()
                .stream()
                .filter(menuId -> !existingMenuIds.contains(menuId))
                .map(menuId -> new SysRoleMenu(adminRole.getId(), menuId))
                .forEach(sysRoleMenuMapper::insert);
    }

    private record RbacSeedData(OrgSeed org,
                                RoleSeed adminRole,
                                UserSeed adminUser,
                                List<MenuSeed> menus) {
    }

    private record OrgSeed(Long parentId,
                           String orgName,
                           String orgCode,
                           Integer sort) {
    }

    private record RoleSeed(String roleName,
                            String roleCode,
                            String description) {
    }

    private record UserSeed(String username,
                            String rawPassword) {
    }

    /**
     * parentPath 使用路由路径表达父子关系，避免在种子数据文件中硬编码数据库自增 ID。
     */
    private record MenuSeed(String parentPath,
                            String menuName,
                            String path,
                            Integer menuType,
                            String menuUrl,
                            String menuIconUrl,
                            Integer sortNo) {
    }
}
