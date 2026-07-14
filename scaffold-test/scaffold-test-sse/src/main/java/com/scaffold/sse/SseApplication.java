package com.scaffold.sse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SseApplication {

    public static void main(String[] args) {
        SpringApplication.run(SseApplication.class, args);
    }

}
