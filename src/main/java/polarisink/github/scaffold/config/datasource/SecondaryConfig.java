package polarisink.github.scaffold.config.datasource;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

/**
 * @author lqs
 * @date 2022/3/14
 */
@Configuration
@RequiredArgsConstructor
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactorySecondary", transactionManagerRef = "transactionManagerSecondary", basePackages = {"polarisink.github.scaffold.repo.mysql.secondary"})
public class SecondaryConfig {
  @Qualifier("secondaryDataSource")
  private final DataSource secondaryDataSource;
  private final JpaProperties jpaProperties;
  private final HibernateProperties hibernateProperties;

  private Map<String, Object> getVendorProperties() {
    return hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
  }

  @Bean(name = "entityManagerSecondary")
  public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
    return Objects.requireNonNull(entityManagerFactorySecondary(builder).getObject()).createEntityManager();
  }

  @Bean(name = "entityManagerFactorySecondary")
  public LocalContainerEntityManagerFactoryBean entityManagerFactorySecondary(EntityManagerFactoryBuilder builder) {
    //设置实体类所在位置
    return builder.dataSource(secondaryDataSource).packages("polarisink.github.scaffold.entity.mysql.secondary").persistenceUnit("secondaryPersistenceUnit").properties(getVendorProperties()).build();
  }

  @Bean(name = "transactionManagerSecondary")
  PlatformTransactionManager transactionManagerSecondary(EntityManagerFactoryBuilder builder) {
    return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactorySecondary(builder).getObject()));
  }

}

