package com.scaffold.vertx;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class SpringVertxApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringVertxApplication.class, args);
    }
}
