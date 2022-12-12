package github.polarisink.dao.config;


import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author lqs
 * @date 2022/3/14
 */
@Configuration
@EnableTransactionManagement
public class TdConfig {

  @Bean
  public JdbcTemplate jdbcTemplateOne(@Qualifier("tdDataSource") DataSource tdDataSource) {
    return new JdbcTemplate(tdDataSource);
  }
}

