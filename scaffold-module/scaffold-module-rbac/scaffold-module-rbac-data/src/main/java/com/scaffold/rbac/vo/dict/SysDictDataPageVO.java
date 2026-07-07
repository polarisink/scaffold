package com.scaffold.rbac.vo.dict;

import com.scaffold.base.util.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "字典数据分页对象")
public class SysDictDataPageVO extends PageRequest {
    private String dictType = "";
    private String dictLabel = "";
    private Boolean status;
}
