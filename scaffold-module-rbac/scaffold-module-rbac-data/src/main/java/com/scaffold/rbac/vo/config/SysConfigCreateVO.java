package com.scaffold.rbac.vo.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "SysConfig新增对象")
public record SysConfigCreateVO(
        @NotBlank(message = "配置名称不能为空") String configName,
        @NotBlank(message = "配置键不能为空") String configKey,
        @NotBlank(message = "配置值不能为空") String configValue,
        Boolean sysFlag,
        String remark) {
}
