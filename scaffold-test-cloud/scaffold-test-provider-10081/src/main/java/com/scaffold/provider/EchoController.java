package com.scaffold.provider;

import java.time.Instant;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
class EchoController {

    @GetMapping("/echo")
    Map<String, String> echo(@RequestParam(name = "message", defaultValue = "world") String message) {
        return Map.of(
                "message", message,
                "provider", "cloud-provider",
                "timestamp", Instant.now().toString());
    }
}
