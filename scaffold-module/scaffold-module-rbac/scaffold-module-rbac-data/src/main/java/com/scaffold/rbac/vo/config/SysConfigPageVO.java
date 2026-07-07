package com.scaffold.rbac.vo.config;

import com.scaffold.base.util.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "SysConfig分页对象")
public class SysConfigPageVO extends PageRequest {
    private String configName = "";
    private String configKey = "";
}
