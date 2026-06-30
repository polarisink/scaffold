package com.scaffold.rbac.vo.user;


import com.scaffold.base.util.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * (SysUser)分页请求类
 *
 * @author aries
 * @since 2024-07-22 20:40:08
 */
@Data
@Schema(name = "SysUser分页对象")
public class SysUserPageVO extends PageRequest implements Serializable {

    @Schema(description = "名字")
    private String username = "";
}

