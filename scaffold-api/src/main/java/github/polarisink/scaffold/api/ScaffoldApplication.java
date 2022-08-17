package github.polarisink.scaffold.api;

import github.polarisink.scaffold.dao.properties.DoubleCacheProperties;
import github.polarisink.scaffold.dao.properties.MinioProperties;
import github.polarisink.scaffold.dao.properties.SwaggerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 主启动类
 *
 * @author aries
 * @date 2022/8/16
 */
@EnableSwagger2
@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableConfigurationProperties({DoubleCacheProperties.class, MinioProperties.class, SwaggerProperties.class})
public class ScaffoldApplication {
  public static void main(String[] args) {
    SpringApplication.run(ScaffoldApplication.class, args);
  }
}
