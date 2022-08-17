package polarisink.github.scaffold.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import polarisink.github.scaffold.bean.request.ArchivesAddRequest;
import polarisink.github.scaffold.entity.mysql.primary.Archives;
import polarisink.github.scaffold.repo.mysql.primary.ArchivesRepo;

/**
 * 实例service
 * @author aries
 * @date 2022/8/17
 */
@Service
@RequiredArgsConstructor
public class TestService {
  private final ArchivesRepo archivesRepo;
  private final ConversionService conversionService;

  @Transactional(rollbackFor = Exception.class)
  public Archives add(ArchivesAddRequest request) {
    Archives archives = conversionService.convert(request, Archives.class);
    return archivesRepo.saveAndFlush(archives);
  }
}
