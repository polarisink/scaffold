package github.polarisink.scaffold.api.config.datasource;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * queryDSL配置
 *
 * @author lqs
 * @date 2022/4/6
 */
@Configuration
public class QueryDslConfig {
  @PersistenceContext
  private final EntityManager entityManager;

  /**
   * 有配置多数据源,此处不指定Bean的名字
   *
   * @param entityManager
   */
  public QueryDslConfig(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Bean
  public JPAQueryFactory jpaQueryFactory() {
    return new JPAQueryFactory(entityManager);
  }
}

