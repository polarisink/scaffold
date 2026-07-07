package ${table.packageName}.${javaBusinessName};

import com.baomidou.mybatisplus.annotation.TableName;
import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
<#list entityImports as item>
    import ${item};
</#list>

/**
* ${table.tableComment!table.tableName}
*/
@Data
@TableName("${table.tableName}")
@Table(name = "${table.tableName}")
@Entity
public class ${className} extends BaseAuditable {
<#list businessColumns as column>
    /** ${column.columnComment!column.columnName} */
    @Column(name = "${column.columnName}", unique = ${(column.uniqueKey!false)?string("true", "false")}, columnDefinition = "${column.columnType}<#if !column.nullable> not null</#if> comment '${column.columnComment!""}'")
    private ${column.javaType?contains(".")?then(column.javaType?keep_after_last("."), column.javaType)} ${column.propertyName};
</#list>
}
