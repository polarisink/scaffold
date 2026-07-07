package ${table.packageName}.${javaBusinessName};

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scaffold.base.util.PageResponse;
import com.scaffold.orm.MyBaseMapper;
import com.scaffold.orm.starter.PageUtils;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ${className}Mapper extends MyBaseMapper<${className}> {

default PageResponse<${className}> page(${className}QueryDTO query) {
Page<${className}> page = selectPage(
new Page<>(query.getPageNo(), query.getPageSize()),
Wrappers.<${className}>lambdaQuery()
<#list columns as column>
    <#if column.queryable>
        <#if column.javaType == "String">
            .like(StrUtil.isNotBlank(query.get${column.propertyName?cap_first}()), ${className}::get${column.propertyName?cap_first}, query.get${column.propertyName?cap_first}())
        <#else>
            .eq(query.get${column.propertyName?cap_first}() != null, ${className}::get${column.propertyName?cap_first}, query.get${column.propertyName?cap_first}())
        </#if>
    </#if>
</#list>
.orderByDesc(${className}::getGmtModified));
return PageUtils.of(page);
}
<#if hasUniqueColumns>
    <#list uniqueColumns as column>

        default ${className} selectBy${column.propertyName?cap_first}(${column.javaType?contains(".")?then(column.javaType?keep_after_last("."), column.javaType)} ${column.propertyName}) {
        return selectOne(Wrappers.<${className}>lambdaQuery()
        .eq(${className}::get${column.propertyName?cap_first}, ${column.propertyName})
        .last("limit 1"));
        }
    </#list>
</#if>
}
