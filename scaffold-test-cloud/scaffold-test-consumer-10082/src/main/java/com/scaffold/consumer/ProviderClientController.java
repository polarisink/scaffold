package com.scaffold.consumer;

import java.time.Instant;
import java.util.Map;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class ProviderClientController {
    private final RemoteService remoteService;

    @GetMapping("/api/provider-echo")
    @SentinelResource(value = "consumer-provider-echo",
            blockHandler = "providerEchoBlocked",
            fallback = "providerEchoFallback")
    public Map<String, String> providerEcho(@RequestParam(name = "message", defaultValue = "world") String message) {
        return remoteService.echo(message).getData();
    }

    public Map<String, String> providerEchoBlocked(String message, BlockException exception) {
        return Map.of(
                "message", message,
                "sentinel", "blocked",
                "reason", exception.getClass().getSimpleName(),
                "timestamp", Instant.now().toString());
    }

    public Map<String, String> providerEchoFallback(String message, Throwable throwable) {
        return Map.of(
                "message", message,
                "sentinel", "fallback",
                "reason", throwable.getClass().getSimpleName(),
                "timestamp", Instant.now().toString());
    }
}
