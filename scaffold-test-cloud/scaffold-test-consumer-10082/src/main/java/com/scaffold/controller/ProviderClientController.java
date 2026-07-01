package com.scaffold.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.scaffold.base.util.R;
import com.scaffold.remote.RemoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "远程调用")
class ProviderClientController {
    private final RemoteService remoteService;

    @Operation(summary = "echo调用")
    @GetMapping("/api/provider-echo")
    @SentinelResource(value = "consumer-provider-echo", blockHandler = "providerEchoBlocked", fallback = "providerEchoFallback")
    public R<Map<String, String>> providerEcho(@RequestParam(name = "message", defaultValue = "world") String message) {
        return R.success(remoteService.echo(message).getData());
    }

    public R<Map<String, String>> providerEchoBlocked(String message, BlockException exception) {
        Map<String, String> map = Map.of(
                "message", message,
                "sentinel", "blocked",
                "reason", exception.getClass().getSimpleName(),
                "timestamp", Instant.now().toString());
        return R.success(map);
    }

    public R<Map<String, String>> providerEchoFallback(String message, Throwable throwable) {
        Map<String, String> map = Map.of(
                "message", message,
                "sentinel", "fallback",
                "reason", throwable.getClass().getSimpleName(),
                "timestamp", Instant.now().toString());
        return R.success(map);
    }
}
