package github.polarisink.dao.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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
  @PersistenceContext
  private final EntityManager secondEntityManager;

  /**
   * 有配置多数据源,此处不指定Bean的名字
   *
   * @param entityManager
   */
  public QueryDslConfig(@Qualifier("entityManagerPrimary") EntityManager entityManager, @Qualifier("entityManagerSecondary") EntityManager secondEntityManager) {
    this.entityManager = entityManager;
    this.secondEntityManager = secondEntityManager;
  }

  @Bean
  @Primary
  public JPAQueryFactory jpaQueryFactory() {
    return new JPAQueryFactory(entityManager);
  }

  @Bean("second")
  public JPAQueryFactory secondJpaQueryFactory() {
    return new JPAQueryFactory(secondEntityManager);
  }
}

