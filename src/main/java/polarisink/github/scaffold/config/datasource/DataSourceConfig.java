package polarisink.github.scaffold.config.datasource;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.druid.primary")
  public DataSource primaryDataSource() {
    LOG.info("Build Primary DataSource...");
    return DruidDataSourceBuilder.create().build();
  }

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.druid.secondary")
  public DataSource secondaryDataSource() {
    LOG.info("Build Secondary DataSource...");
    return DruidDataSourceBuilder.create().build();
  }
}
