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

    @Schema(description = "搜索关键词（用户名、组织机构名、角色名）")
    private String keyword = "";

    @Schema(description = "排序字段：username、orgName、status、modifiedTime、createdTime", defaultValue = "modifiedTime")
    private String sortBy = "modifiedTime";

    @Schema(description = "排序方向：asc、desc", defaultValue = "desc")
    private String sortOrder = "desc";
}
