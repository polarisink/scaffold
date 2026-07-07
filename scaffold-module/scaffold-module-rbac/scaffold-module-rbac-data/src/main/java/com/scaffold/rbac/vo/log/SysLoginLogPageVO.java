package com.scaffold.rbac.vo.log;

import com.scaffold.base.util.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "登录日志分页对象")
public class SysLoginLogPageVO extends PageRequest {
    private String username = "";
    private String ip = "";
    private String action = "";
    private Boolean status;
}
