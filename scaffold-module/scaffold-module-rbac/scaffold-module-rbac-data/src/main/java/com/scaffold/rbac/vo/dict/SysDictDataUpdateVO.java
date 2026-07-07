package com.scaffold.rbac.vo.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "字典数据修改对象")
public record SysDictDataUpdateVO(
        @NotNull(message = "字典数据ID不能为空") Long id,
        @NotBlank(message = "字典类型不能为空") String dictType,
        @NotBlank(message = "字典标签不能为空") String dictLabel,
        @NotBlank(message = "字典值不能为空") String dictValue,
        Integer dictSort,
        Boolean status,
        Boolean defaultFlag,
        String tagType,
        String remark) {
}
