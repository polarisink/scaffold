package ${table.packageName}.${javaBusinessName};

import lombok.Data;
<#list createImports as item>
import ${item};
</#list>

@Data
public class ${className}CreateDTO {
<#list columns as column>
<#if column.formVisible>
    private ${column.javaType?contains(".")?then(column.javaType?keep_after_last("."), column.javaType)} ${column.propertyName};
</#if>
</#list>
}
