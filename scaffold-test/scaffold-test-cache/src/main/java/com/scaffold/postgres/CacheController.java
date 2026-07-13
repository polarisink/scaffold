package com.scaffold.postgres;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@RequestMapping("/cache")
@RestController
public class CacheController {

    private final AtomicInteger counter = new AtomicInteger();

    @GetMapping
    @Cacheable(cacheNames = "user", key = "111")
    public User get() {
        return new User("lqs", 11, counter.incrementAndGet(), Instant.now());
    }

    public record User(String username, int age, int invocation, Instant createdAt) {

    }
}
