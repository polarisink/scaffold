package com.scaffold.vertx.server;

import io.vertx.core.buffer.Buffer;
import lombok.Builder;

@Builder
public record UdpMsgVo(String host, int port, String name, Buffer buffer) {
}
