package github.polarisink.dao.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * @author lqs
 * @date 2022/3/14
 */
@Slf4j
@Configuration
public class DataSourceConfig {
  @Primary
  @Bean("primaryDataSource")
  @ConfigurationProperties(prefix = "spring.datasource.druid.primary")
  public DataSource primaryDataSource() {
    LOG.info("Build Primary DataSource...");
    return DataSourceBuilder.create().build();
  }

  @Bean("secondaryDataSource")
  @ConfigurationProperties(prefix = "spring.datasource.druid.secondary")
  public DataSource secondaryDataSource() {
    LOG.info("Build Secondary DataSource...");
    return DataSourceBuilder.create().build();
  }
}
