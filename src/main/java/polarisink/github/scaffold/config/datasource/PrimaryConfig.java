package polarisink.github.scaffold.config.datasource;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
@EnableTransactionManagement
@RequiredArgsConstructor
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactoryPrimary", transactionManagerRef = "transactionManagerPrimary", basePackages = {"polarisink.github.scaffold.repo.mysql.primary"})
public class PrimaryConfig {

  @Qualifier("primaryDataSource")
  private final DataSource primaryDataSource;
  private final JpaProperties jpaProperties;
  private final HibernateProperties hibernateProperties;

  private Map<String, Object> getVendorProperties() {
    return hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
  }

  @Primary
  @Bean(name = "entityManagerPrimary")
  public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
    return Objects.requireNonNull(entityManagerFactoryPrimary(builder).getObject()).createEntityManager();
  }

  @Primary
  @Bean(name = "entityManagerFactoryPrimary")
  public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary(EntityManagerFactoryBuilder builder) {
    //设置实体类所在位置
    return builder.dataSource(primaryDataSource).packages("polarisink.github.scaffold.entity.mysql.primary").persistenceUnit("primaryPersistenceUnit").properties(getVendorProperties()).build();
  }

  @Primary
  @Bean(name = "transactionManagerPrimary")
  public PlatformTransactionManager transactionManagerPrimary(EntityManagerFactoryBuilder builder) {
    return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactoryPrimary(builder).getObject()));
  }

}
