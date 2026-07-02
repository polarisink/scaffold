package com.scaffold.rbac.vo.org;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "SysOrg修改对象")
public record SysOrgUpdateVO(
        @NotNull(message = "组织ID不能为空") Long id,
        @NotNull(message = "上级组织不能为空") Long parentId,
        @NotBlank(message = "组织名称不能为空") String orgName,
        @NotBlank(message = "组织编码不能为空") String orgCode,
        Integer sort) {
}
