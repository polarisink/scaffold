package github.polarisink;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


/**
 * @author lqs
 */
@SpringBootApplication(exclude = {
        MybatisPlusAutoConfiguration.class,
        RabbitAutoConfiguration.class,
        RedisAutoConfiguration.class,
        RedissonAutoConfiguration.class})
@EnableJpaAuditing
public class ScaffoldApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScaffoldApplication.class, args);
    }

}
