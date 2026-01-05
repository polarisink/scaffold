package com.scaffold.rbac.components;

import com.scaffold.rbac.mapper.SysRoleMapper;
import com.scaffold.rbac.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


/**
 * 初始化rbac的缓存
 *
 * @author lqsgo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RbacCacheInit implements ApplicationRunner {
    private final SysRoleMapper roleMapper;
    private final SysUserMapper userMapper;
    private final RbacCache rbacCache;

    @Override
    public void run(ApplicationArguments args) {
        try {
            //检查是否有这两个角色，没有就新增，这两个角色没有菜单
            //缓存菜单树
            rbacCache.menuTree();
            //缓存组织机构数
            //缓存角色
            roleMapper.findAll().forEach(r -> rbacCache.roleWrapper(r.getId()));
            //只给可用用户加载缓存
            userMapper.selectAllEnabledUserId().forEach(rbacCache::userTree);
            log.info("rbac cache init success...");
        } catch (Exception e) {
            log.error("rbac cache init error: {}", e.getMessage());
        }
    }
}
