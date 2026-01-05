package com.scaffold.hl.api;

import org.springframework.http.MediaType;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WebApi {
    @GetExchange("/api/hello")
    Mono<String> hello();

    @PostExchange(value = "/api/sse", contentType = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<String> sse();
}
