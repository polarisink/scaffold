package github.polarisink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
@SpringBootApplication
public class ScaffoldApplication {
  public static void main(String[] args) {
    SpringApplication.run(ScaffoldApplication.class, args);
  }
}
