package com.scaffold.swagger.config;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 打印文档地址
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DocumentRunner implements ApplicationRunner {
    private final Environment env;

    @Override
    public void run(ApplicationArguments args) {
        String protocol = env.getProperty("server.ssl.key-store") == null ? "http" : "https";
        String serverPort = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "/doc.html";
        } else {
            contextPath = contextPath + "/doc.html";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        String appName = env.getProperty("spring.application.name", "SpringBoot后端服务");
        log.info("""
                \n----------------------------------------------------------
                应用程序【{}】正在运行中......
                接口文档访问 URL:
                本地:     {}://localhost:{}{}
                外部:     {}://{}:{}{}
                配置文件: {}
                ----------------------------------------------------------""", appName, protocol, serverPort, contextPath, protocol, hostAddress, serverPort, contextPath, env.getActiveProfiles());
    }
}
