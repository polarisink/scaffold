package ${table.packageName}.${javaBusinessName};

import cn.hutool.core.util.StrUtil;
import com.scaffold.base.exception.BaseException;
import com.scaffold.base.util.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ${className}Service {

private final ${className}Mapper ${lowerClassName}Mapper;

@Transactional(readOnly = true)
public PageResponse<${className}> page(${className}QueryDTO query) {
return ${lowerClassName}Mapper.page(query);
}

@Transactional(readOnly = true)
public ${className} getById(Long id) {
return ${lowerClassName}Mapper.selectById(id);
}

@Transactional(rollbackFor = Exception.class)
public void save(${className}CreateDTO createDTO) {
${className} entity = new ${className}();
BeanUtils.copyProperties(createDTO, entity);
validateUniqueFieldsOnCreate(entity);
${lowerClassName}Mapper.insert(entity);
}

@Transactional(rollbackFor = Exception.class)
public void updateById(${className} entity) {
if (entity.getId() == null) {
throw new BaseException("ID 不能为空");
}
validateUniqueFieldsOnUpdate(entity);
${lowerClassName}Mapper.updateById(entity);
}

@Transactional(rollbackFor = Exception.class)
public void deleteById(Long id) {
${lowerClassName}Mapper.deleteById(id);
}

private void validateUniqueFieldsOnCreate(${className} entity) {
<#if hasUniqueColumns>
    <#list uniqueColumns as column>
        <#if column.javaType == "String">
            if (StrUtil.isNotBlank(entity.get${column.propertyName?cap_first}())) {
        <#else>
            if (entity.get${column.propertyName?cap_first}() != null) {
        </#if>
        ${className} existed = ${lowerClassName}Mapper.selectBy${column.propertyName?cap_first}(entity.get${column.propertyName?cap_first}());
        if (existed != null) {
        throw new BaseException("${column.columnComment!column.columnName}已存在");
        }
        }
    </#list>
</#if>
}

private void validateUniqueFieldsOnUpdate(${className} entity) {
<#if hasUniqueColumns>
    <#list uniqueColumns as column>
        <#if column.javaType == "String">
            if (StrUtil.isNotBlank(entity.get${column.propertyName?cap_first}())) {
        <#else>
            if (entity.get${column.propertyName?cap_first}() != null) {
        </#if>
        ${className} existed = ${lowerClassName}Mapper.selectBy${column.propertyName?cap_first}(entity.get${column.propertyName?cap_first}());
        if (existed != null && !Objects.equals(existed.getId(), entity.getId())) {
        throw new BaseException("${column.columnComment!column.columnName}已存在");
        }
        }
    </#list>
</#if>
}
}
