package com.scaffold.udp.server;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "udp")
public record UdpProperties(String receiverHost, String localhost, int port) {
    public String getReceiverHost(){ return receiverHost; }
    public String getLocalhost(){ return localhost; }
    public int getPort(){ return port; }
}
