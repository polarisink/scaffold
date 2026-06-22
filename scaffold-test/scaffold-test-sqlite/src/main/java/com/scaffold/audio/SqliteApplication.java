package com.scaffold.audio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class SqliteApplication {
    public static void main(String[] args) {
        SpringApplication.run(SqliteApplication.class, args);
    }
}
