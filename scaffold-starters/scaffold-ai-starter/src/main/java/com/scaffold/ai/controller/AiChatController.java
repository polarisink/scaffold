package com.scaffold.ai.controller;

import com.scaffold.ai.chat.AiChatService;
import com.scaffold.ai.config.ScaffoldAiProperties;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/api/ai/chat")
public class AiChatController {

    private final AiChatService service;
    private final ScaffoldAiProperties properties;

    public AiChatController(AiChatService service, ScaffoldAiProperties properties) {
        this.service = service;
        this.properties = properties;
    }

    @PostMapping
    public Map<String, String> chat(@RequestBody ChatRequest request) {
        return Map.of("content", service.chat(request.conversationId(), system(request.system()), request.message()));
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestBody ChatRequest request) {
        return service.stream(request.conversationId(), system(request.system()), request.message());
    }

    private String system(String value) {
        return value == null || value.isBlank() ? properties.getSystemPrompt() : value;
    }

    public record ChatRequest(String conversationId, String system, String message) {}
}
