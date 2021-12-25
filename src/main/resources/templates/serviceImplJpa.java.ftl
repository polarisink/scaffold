package ${package.ServiceImpl};

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
<#--import ${package.Entity}.${entity};-->
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};
import org.springframework.stereotype.Service;

/**
 * <p>
 * ${table.comment!} 服务实现类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Service
<#if kotlin>
open class ${table.serviceImplName} : ${superServiceImplClass}<${table.mapperName}, ${entity}>(), ${table.serviceName} {

}
<#else>
public class ${table.serviceImplName} implements ${table.serviceName} {
  private static final Logger log = LoggerFactory.getLogger(${table.serviceImplName}.class);
  private final ${table.mapperName} ${table.mapperName?uncap_first};

  public ${table.serviceImplName}(${table.mapperName} ${table.mapperName?uncap_first}){
    this. ${table.mapperName?uncap_first}= ${table.mapperName?uncap_first};
  }
}
</#if>
