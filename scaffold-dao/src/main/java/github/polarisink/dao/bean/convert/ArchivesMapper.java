package github.polarisink.dao.bean.convert;

import github.polarisink.dao.bean.request.ArchivesAddRequest;
import github.polarisink.dao.entity.primary.Archives;
import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;

/**
 * @author aries
 * @date 2022/5/16
 */
@Mapper(componentModel = "spring")
public interface ArchivesMapper extends Converter<ArchivesAddRequest, Archives> {
  Archives convert(ArchivesAddRequest request);
}
