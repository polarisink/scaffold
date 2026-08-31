package com.scaffold.audio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(excludeName = "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration")
public class SqliteApplication {
    public static void main(String[] args) {
        SpringApplication.run(SqliteApplication.class, args);
    }
}
