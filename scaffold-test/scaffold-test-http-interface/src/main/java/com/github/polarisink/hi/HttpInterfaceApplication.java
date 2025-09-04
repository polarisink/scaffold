package com.github.polarisink.hi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;

@SpringBootApplication(exclude = WebFluxAutoConfiguration.class)
public class HttpInterfaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HttpInterfaceApplication.class, args);
    }

}
