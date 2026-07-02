package com.scaffold.rbac.vo.dict;

import com.scaffold.base.util.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "字典类型分页对象")
public class SysDictTypePageVO extends PageRequest {
    private String dictName = "";
    private String dictType = "";
    private Boolean status;
}
