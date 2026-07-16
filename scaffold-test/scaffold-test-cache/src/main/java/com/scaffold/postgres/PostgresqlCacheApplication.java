package com.scaffold.postgres;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class PostgresqlCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostgresqlCacheApplication.class, args);
    }
}
