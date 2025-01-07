package com.scaffold.socket.config;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * socket服务配置
 *
 * @author machenike
 */
@Configuration
@RequiredArgsConstructor
public class NettySocketConfig {
    private final AuthorizationListener authorizationListener;

    @Value("${train.socketio.host:127.0.0.1}")
    private String host;

    @Value("${train.socketio.port:8080}")
    private Integer port;
    @Value("${train.socketio.context:}")
    private String contextPath = "";

    /**
     * netty-socketio服务器
     *
     * @return
     **/
    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        //设置host
        config.setHostname(host);
        //设置端口
        config.setPort(port);
        //设置路径
        if (contextPath != null && !contextPath.isBlank()) {
            config.setContext(contextPath);
        }
        //初始化认证监听器
        //设置认证监听器
        config.setAuthorizationListener(authorizationListener);
        SocketIOServer server = new SocketIOServer(config);
        server.start();
        return server;
    }

    /**
     * 用于扫描netty-socketio的注解，比如 @OnConnect、@OnEvent
     **/
    @Bean
    public SpringAnnotationScanner springAnnotationScanner() {
        return new SpringAnnotationScanner(socketIOServer());
    }

}
