package com.scaffold.qwen3asr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Qwen3AsrApplication {

    public static void main(String[] args) {
        SpringApplication.run(Qwen3AsrApplication.class, args);
    }
}
