package com.scaffold.socket.config;

import com.corundumstudio.socketio.Transport;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Socket.IO server settings. */
@ConfigurationProperties("scaffold.websocket")
public record WebSocketProperties(
        String host,
        Integer port,
        String context,
        Transport[] transports) {

    public WebSocketProperties {
        host = host == null || host.isBlank() ? "127.0.0.1" : host;
        port = port == null || port < 0 ? 8081 : port;
        context = context == null ? "" : context;
        transports = transports == null || transports.length == 0
                ? new Transport[]{Transport.WEBSOCKET}
                : transports.clone();
    }

    @Override
    public Transport[] transports() {
        return transports.clone();
    }
}
