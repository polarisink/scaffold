export interface ${className} {
<#list columns as column>
    ${column.propertyName}?: ${column.tsType};
</#list>
}

export type ${className}Save = Partial<${className}>;
