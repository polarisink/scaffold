package com.scaffold.vertx.server;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * 网络配置
 */
@ConfigurationProperties(prefix = "net")
public record NetProperties(
        @DefaultValue("8076") Integer wsPort, @DefaultValue("0.0.0.0") String wsHost,
        @DefaultValue("8077") Integer udpPort, @DefaultValue("0.0.0.0") String udpHost,
        @DefaultValue("8078") Integer tcpPort, @DefaultValue("0.0.0.0") String tcpHost,
        @DefaultValue("8079") Integer httpPort, @DefaultValue("0.0.0.0") String httpHost) {
    public Integer getWsPort(){ return wsPort; } public String getWsHost(){ return wsHost; }
    public Integer getUdpPort(){ return udpPort; } public String getUdpHost(){ return udpHost; }
    public Integer getTcpPort(){ return tcpPort; } public String getTcpHost(){ return tcpHost; }
    public Integer getHttpPort(){ return httpPort; } public String getHttpHost(){ return httpHost; }
}
