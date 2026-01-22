package com.scaffold.vertx.config;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class VertxConfig {


    @Bean
    public Vertx vertx(SpringVerticleFactory factory) {
        Vertx vertx = Vertx.vertx(new VertxOptions());
        vertx.registerVerticleFactory(factory); // 注册工厂
        log.info("Vert.x instance created and factory registered");
        return vertx;
    }

}