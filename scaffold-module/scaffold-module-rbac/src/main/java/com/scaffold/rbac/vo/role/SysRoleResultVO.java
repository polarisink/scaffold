package com.scaffold.rbac.vo.role;


import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 角色(SysRole)结果类
 *
 * @param id          id
 * @param roleName    角色名
 * @param roleCode    角色编码
 * @param description 描述
 * @param gmtCreated  创建时间
 * @param gmtModified 修改时间
 * @param creatorId   创建者id
 * @param menderId    修改者id
 * @author aries
 * @since 2024-07-22 20:38:40
 */
@Schema(name = "SysRole结果对象", description = "角色")
public record SysRoleResultVO(

        @Schema(description = "id") String id,

        @Schema(description = "角色名") String roleName,

        @Schema(description = "角色编码") String roleCode,

        @Schema(description = "描述") String description,

        @Schema(description = "创建时间") LocalDateTime gmtCreated,

        @Schema(description = "修改时间") LocalDateTime gmtModified,

        @Schema(description = "创建人") String creatorId,

        @Schema(description = "修改人") String menderId) {
}

