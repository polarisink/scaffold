package github.polarisink.api.service;

import github.polarisink.dao.entity.primary.Archives;
import github.polarisink.dao.repo.primary.ArchivesRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author aries
 * @date 2022/8/17
 */
@Service
@RequiredArgsConstructor
public class TestService {

  private final ArchivesRepo archivesRepo;
  @Transactional(rollbackFor = Exception.class)

  public Archives getById(Long id) {
    return archivesRepo.findByIdSafe(id);
  }

  public void addArchive(Archives archives) {
    archivesRepo.save(archives);
  }
}
