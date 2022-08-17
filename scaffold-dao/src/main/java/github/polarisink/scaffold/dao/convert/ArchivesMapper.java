package github.polarisink.scaffold.dao.convert;

import github.polarisink.scaffold.dao.request.ArchivesAddRequest;
import org.mapstruct.Mapper;
import github.polarisink.scaffold.dao.entity.primary.Archives;
import org.springframework.core.convert.converter.Converter;

/**
 * @author aries
 * @date 2022/5/16
 */
@Mapper(componentModel = "spring")
public interface ArchivesMapper extends Converter<ArchivesAddRequest, Archives> {
  Archives convert(ArchivesAddRequest request);
}
