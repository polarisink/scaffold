package com.scaffold.socket.config;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.corundumstudio.socketio.handler.SuccessAuthorizationListener;
import com.scaffold.socket.util.WsManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/** Auto-configures an embedded Netty Socket.IO server. */
@AutoConfiguration
@ConditionalOnClass(SocketIOServer.class)
@ConditionalOnProperty(prefix = "scaffold.websocket", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(WebSocketProperties.class)
public class WebSocketAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    AuthorizationListener socketAuthorizationListener() {
        return new SuccessAuthorizationListener();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean
    SocketIOServer socketIOServer(WebSocketProperties properties, AuthorizationListener authorizationListener) {
        com.corundumstudio.socketio.Configuration configuration =
                new com.corundumstudio.socketio.Configuration();
        configuration.setHostname(properties.host());
        configuration.setPort(properties.port());
        // Allow a recently closed server socket to be rebound during a normal application restart.
        configuration.getSocketConfig().setReuseAddress(true);
        if (!properties.context().isBlank()) {
            configuration.setContext(properties.context());
        }
        configuration.setTransports(properties.transports());
        configuration.setAuthorizationListener(authorizationListener);
        return new SocketIOServer(configuration);
    }

    @Bean
    @ConditionalOnMissingBean
    static SpringAnnotationScanner springAnnotationScanner(SocketIOServer server) {
        return new SpringAnnotationScanner(server);
    }

    @Bean
    @ConditionalOnMissingBean
    WsManager wsManager(SocketIOServer server) {
        return new WsManager(server);
    }
}
