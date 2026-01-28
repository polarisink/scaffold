package com.scaffold.vertx.config;

import io.vertx.core.Deployable;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.spi.VerticleFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

@Component
@RequiredArgsConstructor
public class SpringVerticleFactory implements VerticleFactory {

    private final ApplicationContext applicationContext;

    @Override
    public String prefix() {
        return "spring-vertx:";
    }

    @Override
    public void createVerticle2(String verticleName, ClassLoader classLoader, Promise<Callable<? extends Deployable>> promise) {
        try {
            String clazzName = VerticleFactory.removePrefix(verticleName);
            Class<?> clazz = Class.forName(clazzName);
            Object bean = applicationContext.getBean(clazz);
            if (bean instanceof Verticle) {
                promise.complete(() -> (Verticle) bean);
            } else {
                promise.fail(new IllegalArgumentException("Bean is not a Verticle: " + clazzName));
            }
        } catch (Exception e) {
            promise.fail(e);
        }
    }


}