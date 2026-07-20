package com.scaffold;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

//@EntityScan(basePackages = {"com.scaffold.ai", "com.scaffold.rbac.entity"})
@SpringBootApplication
public class SupportAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupportAiApplication.class, args);
    }
}
