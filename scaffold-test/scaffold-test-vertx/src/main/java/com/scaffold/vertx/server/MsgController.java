package com.scaffold.vertx.server;

import io.vertx.core.buffer.Buffer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/msg")
@RequiredArgsConstructor
public class MsgController {
    private final MsgService msgService;

    @GetMapping
    public void send() {
        UdpMsgVo udpMsgVo = UdpMsgVo.builder().name("test").host("127.0.0.1").port(8080).buffer(Buffer.buffer("hello udp")).build();
        msgService.sendUdpMsg(udpMsgVo);
    }
}
