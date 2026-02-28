package com.scaffold.socket.config;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

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

    @Value("${train.socketio.port:8081}")
    private Integer port;
    @Value("${train.socketio.context:}")
    private String contextPath = "";

    /**
     * netty-socketio服务器
     * 如果是key.pem和cert的pem，先合并为pkcs12文件
     * openssl pkcs12 -export -in cert.pem -inkey key.pem -out keystore.p12 -name "socketio" -passout pass:123456
     *
     * @return
     **/
    @Bean
    public SocketIOServer socketIOServer() throws IOException {
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
        // 开启 SSL
        /*config.setKeyStoreFormat("PKCS12");
        config.setKeyStore(resourceLoader.getResource("classpath:keystore.p12").getInputStream());
        // 刚才设置的密码
        config.setKeyStorePassword("123456");
         */
        config.setAuthorizationListener(authorizationListener);
        SocketIOServer server = new SocketIOServer(config);
        server.start();
        return server;
    }

    /**
     * 用于扫描netty-socketio的注解，比如 @OnConnect、@OnEvent
     **/
    @Bean
    public SpringAnnotationScanner springAnnotationScanner() throws IOException {
        return new SpringAnnotationScanner(socketIOServer());
    }

}
