package polarisink.github.scaffold.repo.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import polarisink.github.scaffold.core.exception.BusinessException;

/**
 * jpaRepo基本封装,避免编写findById每次抛出异常的样板代码
 *
 * @author aries
 * @date 2022/5/23
 */
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
   * findById的封装,不用每次orElseThrow
   *
   * @param id
   * @return
   */
  default T findByIdSafe(Long id) {
    return findById(id).orElseThrow(() -> new BusinessException("表中不存在ID为[{}]的数据", id));
  }

  /**
   * 带有format和msg
   *
   * @param id
   * @param format
   * @param args
   * @return
   */
  default T findByIdSafe(Long id, String format, Object... args) {
    return findById(id).orElseThrow(() -> new BusinessException(format, args));
  }

}
