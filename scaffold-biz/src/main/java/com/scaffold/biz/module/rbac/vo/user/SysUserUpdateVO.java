package com.scaffold.biz.module.rbac.vo.user;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * (SysUser)更新请求类
 *
 * @author aries
 * @since 2024-07-22 20:40:08
 */
@Data
@Schema(name = "SysUser更新对象")
public class SysUserUpdateVO implements Serializable {

    @Schema(description = "id")
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
    private String username;

    @NotBlank(message = "组织不能为空")
    @Schema(description = "组织id")
    private String orgId;

    @NotEmpty(message = "用户必须包含角色")
    @Schema(description = "角色id集合")
    private List<Long> roleIdList;
}

