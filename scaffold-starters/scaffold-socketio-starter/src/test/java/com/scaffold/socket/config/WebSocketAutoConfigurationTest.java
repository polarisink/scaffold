package com.scaffold.socket.config;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.protocol.JacksonJsonSupport;
import com.corundumstudio.socketio.protocol.JsonSupport;
import com.scaffold.socket.util.WsManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class WebSocketAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(WebSocketAutoConfiguration.class))
            .withPropertyValues("scaffold.socketio.port=0");

    @Test
    void configuresSocketIoServerFromProperties() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(SocketIOServer.class);
            assertThat(context).hasSingleBean(AuthorizationListener.class);
            assertThat(context).hasSingleBean(JsonSupport.class);
            assertThat(context).hasSingleBean(WsManager.class);
            assertThat(context).hasSingleBean(WebSocketProperties.class);

            SocketIOServer server = context.getBean(SocketIOServer.class);
            assertThat(server.getConfiguration().getJsonSupport())
                    .isSameAs(context.getBean(JsonSupport.class))
                    .isInstanceOf(JacksonJsonSupport.class);
            assertThat(server.getConfiguration().getSocketConfig().isReuseAddress()).isTrue();

            WebSocketProperties properties = context.getBean(WebSocketProperties.class);
            assertThat(properties.host()).isEqualTo("127.0.0.1");
            assertThat(properties.port()).isZero();
            assertThat(properties.context()).isEmpty();
        });
    }

    @Test
    void usesApplicationAuthorizationListenerWhenProvided() {
        contextRunner.withUserConfiguration(CustomAuthorizationConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(AuthorizationListener.class);
                    assertThat(context.getBean(AuthorizationListener.class))
                            .isSameAs(context.getBean("customAuthorizationListener"));
                });
    }

    @Test
    void canDisableWebSocketServer() {
        contextRunner.withPropertyValues("scaffold.socketio.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(SocketIOServer.class);
                    assertThat(context).doesNotHaveBean(WsManager.class);
                });
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomAuthorizationConfiguration {
        @Bean
        AuthorizationListener customAuthorizationListener() {
            return handshakeData -> new com.corundumstudio.socketio.AuthorizationResult(false);
        }
    }
}
