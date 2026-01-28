package com.scaffold.vertx.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 网络配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "net")
public class NetProperties {
    //多网卡时绑定网卡
    private Integer wsPort = 8076;
    private String wsHost = "0.0.0.0";
    private Integer udpPort = 8077;
    private String udpHost = "0.0.0.0";
    private Integer tcpPort = 8078;
    private String tcpHost = "0.0.0.0";
    private Integer httpPort = 8079;
    private String httpHost = "0.0.0.0";
}
