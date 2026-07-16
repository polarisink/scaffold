package com.scaffold.ai.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.ToolCallback;

import java.util.Map;

public class AiToolInvoker {

    private final AiToolRegistry registry;
    private final ObjectMapper objectMapper;

    public AiToolInvoker(AiToolRegistry registry, ObjectMapper objectMapper) {
        this.registry = registry;
        this.objectMapper = objectMapper;
    }

    public String invoke(String name, Map<String, Object> input) {
        ToolCallback callback = registry.get(name);
        if (callback == null) throw new IllegalArgumentException("Unknown AI tool: " + name);
        try {
            return callback.call(objectMapper.writeValueAsString(input == null ? Map.of() : input));
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Cannot serialize input for AI tool: " + name, exception);
        }
    }
}
