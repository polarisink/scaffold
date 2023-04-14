package github.polarisink;

import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 主启动类
 *
 * @author aries
 * @date 2022/8/16
 */
@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication(exclude = RocketMQAutoConfiguration.class)
@MapperScan("github.polarisink.dao.mapper")
@EnableTransactionManagement
public class ScaffoldApplication {

  public static void main(String[] args) {
    SpringApplication.run(ScaffoldApplication.class, args);
  }
}
