package polarisink.github.scaffold.bean.convert;

import org.springframework.core.convert.converter.Converter;
import polarisink.github.scaffold.bean.request.ArchivesAddRequest;
import org.mapstruct.Mapper;
import polarisink.github.scaffold.entity.mysql.primary.Archives;

/**
 * @author aries
 * @date 2022/5/16
 */
@Mapper(componentModel = "spring")
public interface ArchivesMapper extends Converter<ArchivesAddRequest, Archives> {
  Archives convert(ArchivesAddRequest request);
}
