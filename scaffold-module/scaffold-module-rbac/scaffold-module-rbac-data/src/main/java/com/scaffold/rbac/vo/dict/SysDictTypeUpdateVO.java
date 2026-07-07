package com.scaffold.rbac.vo.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "字典类型修改对象")
public record SysDictTypeUpdateVO(
        @NotNull(message = "字典类型ID不能为空") Long id,
        @NotBlank(message = "字典名称不能为空") String dictName,
        @NotBlank(message = "字典类型不能为空") String dictType,
        Boolean status,
        String remark) {
}
