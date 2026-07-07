package ${table.packageName}.${javaBusinessName};

import com.scaffold.base.util.PageRequest;
import lombok.Data;
<#list queryImports as item>
    import ${item};
</#list>

@Data
public class ${className}QueryDTO extends PageRequest {
<#list columns as column>
    <#if column.queryable>
        private ${column.javaType?contains(".")?then(column.javaType?keep_after_last("."), column.javaType)} ${column.propertyName};
    </#if>
</#list>
}
