package com.scaffold.biz.module.rbac.mapper;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.core.api.pagination.EasyPageResult;
import com.scaffold.biz.module.rbac.entity.SysMenu;
import com.scaffold.biz.module.rbac.entity.SysRole;
import com.scaffold.biz.module.rbac.entity.SysRoleMenu;
import com.scaffold.biz.module.rbac.entity.SysUserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class QueryRepo {
    private final EasyEntityQuery query;

    public EasyPageResult<SysMenu> userPage(Long userId) {
        return null;
    }

    /**
     * select distinct m.*
     * from sys_menu m
     * inner join sys_role_menu rm on m.id = rm.menu_id and rm.deleted = 0
     * inner join sys_role r on rm.role_id = r.id and r.deleted = 0
     * inner join training.sys_user_role ur on r.id = ur.role_id and ur.deleted = 0
     * where m.deleted = 0 and ur.user_id = #{userId}
     *
     * @param userId 用户id
     * @return
     */
    public EasyPageResult<SysMenu> findMenuCollByUserId(Long userId) {
        return query.queryable(SysMenu.class).distinct()
                .leftJoin(SysRoleMenu.class, (m, rm) -> {
                    m.id().eq(rm.menuId());
                    rm.deleted().eq(0);
                })
                .leftJoin(SysRole.class, (m, rm, r) -> {
                    rm.roleId().eq(r.id());
                    r.deleted().eq(0);
                })
                .leftJoin(SysUserRole.class, (m, rm, r, ur) -> {
                    r.id().eq(ur.roleId());
                    ur.deleted().eq(0);
                }).where((m, rm, r, ur) -> {
                    m.deleted().eq(0);
                    ur.userId().eq(userId);
                }).toPageResult(1, 20);
    }
}
