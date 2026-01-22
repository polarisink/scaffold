package com.scaffold.vertx.server;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

//todo 如何保证服务启动后发消息
@Component
@RequiredArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE-1)
public class MsgService implements ApplicationRunner {
    private final Vertx vertx;

    @Override
    public void run(ApplicationArguments args) {
        // 这种方式天然保证：如果 Verticle 没启动成功（没注册消费者），消息会自动堆积或报错
        // 且 Vert.x 会自动在多个实例间进行负载均衡
        vertx.eventBus().send(UdpVerticle.UDP_MSG_EVENT, new JsonObject().put("data", new byte[]{1, 2, 3, 4}).put("port", 8080).put("host", "127.0.0.1"));
    }
}
