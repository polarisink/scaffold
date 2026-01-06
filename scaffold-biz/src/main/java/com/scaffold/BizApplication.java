package com.scaffold;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class BizApplication {
    public static void main(String[] args) {
        SpringApplication.run(BizApplication.class, args);
    }
}
