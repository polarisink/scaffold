package com.github.polarisink.hi.api;

import org.springframework.web.service.annotation.GetExchange;

public interface RestApi {
    @GetExchange("/api/hello")
    String hello();
}
