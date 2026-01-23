package com.scaffold.vertx.server;

import io.vertx.core.Vertx;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

//todo 如何保证服务启动后发消息
@Component
@RequiredArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class MsgService {
    private final Vertx vertx;

    public void sendUdpMsg(UdpMsgVo udpMsgVo) {
        // 这种方式天然保证：如果 Verticle 没启动成功（没注册消费者），消息会自动堆积或报错
        // 且 Vert.x 会自动在多个实例间进行负载均衡
        vertx.eventBus().send(UdpVerticle.UDP_MSG_EVENT, udpMsgVo);
    }
}
