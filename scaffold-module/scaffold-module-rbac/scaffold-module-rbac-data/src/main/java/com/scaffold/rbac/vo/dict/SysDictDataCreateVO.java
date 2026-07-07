package com.scaffold.rbac.vo.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "字典数据新增对象")
public record SysDictDataCreateVO(
        @NotBlank(message = "字典类型不能为空") String dictType,
        @NotBlank(message = "字典标签不能为空") String dictLabel,
        @NotBlank(message = "字典值不能为空") String dictValue,
        Integer dictSort,
        Boolean status,
        Boolean defaultFlag,
        String tagType,
        String remark) {
}
