package ${package.Mapper};

import ${package.Entity}.${entity};
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * <p>
 * ${table.comment!} repository 接口
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */

<#if kotlin>
interface ${table.mapperName} : ${superMapperClass}<${entity}>
<#else>
public interface ${table.mapperName} extends JpaRepository<${entity},Long> {

}
</#if>
