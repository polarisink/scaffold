package com.scaffold.biz.module.rbac.vo.role;


import com.scaffold.core.base.util.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色(SysRole)分页请求类
 *
 * @author aries
 * @since 2024-07-22 20:38:40
 */
@Data
@Schema(name = "SysRole分页对象", description = "角色")
public class SysRolePageVO extends PageRequest implements Serializable {

    @Schema(description = "角色名")
    private String roleName = "";

    @Schema(description = "角色编码")
    private String roleCode = "";
}

