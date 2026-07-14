package com.scaffold;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * JNA 测试应用启动类
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class JnaApplication {
    public static void main(String[] args) {
        SpringApplication.run(JnaApplication.class, args);
    }
}
