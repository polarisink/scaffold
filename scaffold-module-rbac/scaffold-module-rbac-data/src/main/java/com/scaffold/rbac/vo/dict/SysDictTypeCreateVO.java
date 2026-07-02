package com.scaffold.rbac.vo.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "字典类型新增对象")
public record SysDictTypeCreateVO(
        @NotBlank(message = "字典名称不能为空") String dictName,
        @NotBlank(message = "字典类型不能为空") String dictType,
        Boolean status,
        String remark) {
}
