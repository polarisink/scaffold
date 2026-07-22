package com.scaffold.socket.config;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.corundumstudio.socketio.handler.SuccessAuthorizationListener;
import com.corundumstudio.socketio.protocol.JacksonJsonSupport;
import com.corundumstudio.socketio.protocol.JsonSupport;
import com.scaffold.base.util.JsonUtil;
import com.scaffold.socket.util.WsManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;

/** Auto-configures an embedded Netty Socket.IO server. */
@AutoConfiguration
@ConditionalOnClass(SocketIOServer.class)
@ConditionalOnProperty(prefix = "scaffold.socketio", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(WebSocketProperties.class)
@ImportRuntimeHints(SocketIoRuntimeHints.class)
public class WebSocketAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    AuthorizationListener socketAuthorizationListener() {
        return new SuccessAuthorizationListener();
    }

    @Bean
    @ConditionalOnMissingBean
    JsonSupport socketJsonSupport() {
        return new JacksonJsonSupport(JsonUtil.getJavaTimeModule());
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean
    SocketIOServer socketIOServer(WebSocketProperties properties, AuthorizationListener authorizationListener,
                                  JsonSupport jsonSupport) {
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
        // netty-socketio otherwise loads JacksonJsonSupport reflectively, which is not reachable in native images.
        configuration.setJsonSupport(jsonSupport);
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
