package github.polarisink;

import io.xream.x7.EnableX7L2Caching;
import io.xream.x7.EnableX7Repository;
import io.xream.x7.repository.id.autoconfigure.IdGeneratorAutoConfiguration;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
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
@EnableX7L2Caching
@EnableX7Repository(mappingPrefix = "t_",mappingSpec = "_")
@SpringBootApplication(exclude = {RocketMQAutoConfiguration.class, IdGeneratorAutoConfiguration.class})
public class ScaffoldApplication {

  public static void main(String[] args) {
    SpringApplication.run(ScaffoldApplication.class, args);
  }
}
