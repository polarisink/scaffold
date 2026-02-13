package com.scaffold;

import cn.hutool.extra.spring.EnableSpringUtil;
import dev.langchain4j.spring.LangChain4jAutoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableSpringUtil
@SpringBootApplication(exclude = LangChain4jAutoConfig.class)
public class LangChain4jApplication {
    public static void main(String[] args) {
        SpringApplication.run(LangChain4jApplication.class, args);
    }
}
