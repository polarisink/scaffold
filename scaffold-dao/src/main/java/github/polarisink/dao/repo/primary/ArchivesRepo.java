package github.polarisink.dao.repo.primary;

import github.polarisink.dao.entity.primary.Archives;
import github.polarisink.dao.repo.BaseJpaRepo;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author aries
 * @date 2022/8/17
 */
public interface ArchivesRepo extends BaseJpaRepo<Archives> {

    @Query("""
            select a.id
            from archives a
            order by a.id desc
            """)
    List<Long> findAllIds();
}
