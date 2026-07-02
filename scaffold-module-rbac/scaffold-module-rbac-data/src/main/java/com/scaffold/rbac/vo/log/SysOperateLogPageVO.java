package com.scaffold.rbac.vo.log;

import com.scaffold.base.util.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "操作日志分页对象")
public class SysOperateLogPageVO extends PageRequest {
    private String title = "";
    private String operator = "";
    private Boolean status;
}
