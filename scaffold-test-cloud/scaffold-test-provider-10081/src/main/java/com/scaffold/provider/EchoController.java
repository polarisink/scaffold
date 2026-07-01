package com.scaffold.provider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@Tag(name = "Echo接口")
@RestController
@RequestMapping("/api")
class EchoController {

    @Operation(summary = "Echo")
    @GetMapping("/echo")
    Map<String, String> echo(@RequestParam(name = "message", defaultValue = "world") String message) {
        return Map.of("message", message, "provider", "cloud-provider", "timestamp", Instant.now().toString());
    }
}
