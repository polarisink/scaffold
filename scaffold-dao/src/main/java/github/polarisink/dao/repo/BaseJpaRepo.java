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
     * @param t
     * @return
     */
    default T findByIdSafe(Long id, T t) {
        return findById(id).orElse(t);
    }

    /**
     * 带有msg的
     *
     * @param id
     * @return
     */
    default T findByIdSafe(Long id) {
        return findByIdSafe(id, null);
    }


    /**
     * 查询失败就抛异常
     *
     * @param id
     * @param format
     * @param args
     * @return
     */
    default T findById(Long id, String format, Object... args) {
        return findById(id).orElseThrow(() -> new BusinessException(format, args));
    }

}
