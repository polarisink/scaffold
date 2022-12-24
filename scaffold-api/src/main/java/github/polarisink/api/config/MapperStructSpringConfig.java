package github.polarisink.api.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.extensions.spring.SpringMapperConfig;

/**
 * mapstruct-spring配置
 *
 * @author aries
 * @date 2022/5/7
 */
@MapperConfig(componentModel = "spring")
@SpringMapperConfig(conversionServiceAdapterPackage = "polarisink.github.config", conversionServiceAdapterClassName = "MapStructConversionServiceAdapter")
public class MapperStructSpringConfig {

}
