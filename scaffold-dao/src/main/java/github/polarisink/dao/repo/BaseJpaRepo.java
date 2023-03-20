package github.polarisink.dao.repo;

import github.polarisink.common.exception.BusinessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author aries
 * @date 2022/5/23
 */
@NoRepositoryBean
public interface BaseJpaRepo<T> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {

    /**
     * 带有msg的
     *
     * @param id
     * @param msg
     * @return
     */
    default T findByIdSafe(Long id, String msg) {
        return findById(id).orElseThrow(() -> new BusinessException(msg));
    }

    /**
     * 带有msg的
     *
     * @param id
     * @return
     */
    default T findByIdSafe(Long id) {
        return findById(id).orElseThrow(() -> new BusinessException("数据库不存在id为{}的实体", id));
    }


    /**
     * @param id
     * @param format
     * @param args
     * @return
     */
    default T findByIdSafe(Long id, String format, Object... args) {
        return findById(id).orElseThrow(() -> new BusinessException(format, args));
    }

}
