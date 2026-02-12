package com.polarisink.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAdminServer
@SpringBootApplication
public class SbAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(SbAdminApplication.class, args);
    }

}
