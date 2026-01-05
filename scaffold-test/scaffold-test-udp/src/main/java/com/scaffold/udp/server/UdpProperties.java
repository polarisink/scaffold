package com.scaffold.udp.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "udp")
public class UdpProperties {
    private String receiverHost;
    private String localhost;
    private int port;
}
