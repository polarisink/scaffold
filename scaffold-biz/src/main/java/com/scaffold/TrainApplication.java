package com.scaffold;

import cn.hutool.extra.spring.EnableSpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * @author aries
 */
@EnableJpaAuditing
@EnableWebSecurity
@EnableSpringUtil
@SpringBootApplication
public class TrainApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrainApplication.class, args);
    }
}