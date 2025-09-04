package com.scaffold.biz.module.rbac.vo.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "角色VO")
public class RoleVO {
    @Schema(description = "角色id")
    private String roleId;
    @Schema(description = "角色名")
    private String roleName;
    @Schema(description = "角色编码")
    private String roleCode;
}