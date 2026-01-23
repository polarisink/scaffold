package com.scaffold;

import cn.hutool.extra.spring.EnableSpringUtil;
import com.scaffold.redis.core.RedisMqSender;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@EnableSpringUtil
@SpringBootApplication
public class RedisApplication implements ApplicationRunner {
    public static void main(String[] args) {
        SpringApplication.run(RedisApplication.class, args);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        RedisMqSender.send("r.lqs", "你干嘛");
    }

    @Bean
    public ScheduledExecutorService executorService() {
        return Executors.newScheduledThreadPool(10);
    }


}
