package com.scaffold.vertx.server;

import io.vertx.core.VerticleBase;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Supplier;

@Data
@AllArgsConstructor
public class ServiceConfig {
    Supplier<? extends VerticleBase> supplier;
    int instances;
    String name;
    int port;
    String host;
}