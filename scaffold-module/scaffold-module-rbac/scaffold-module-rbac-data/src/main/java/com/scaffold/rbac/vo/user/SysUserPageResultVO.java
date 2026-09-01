package com.scaffold.rbac.vo.user;

import com.scaffold.rbac.entity.SysRole;
import com.scaffold.rbac.entity.SysUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 用户分页结果，包含用户在列表中展示所需的角色集合。
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class SysUserPageResultVO extends SysUser {

    private List<SysRole> roles = List.of();
}
