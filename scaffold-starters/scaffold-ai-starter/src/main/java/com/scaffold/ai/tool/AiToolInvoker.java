package com.scaffold.ai.tool;

import tools.jackson.databind.json.JsonMapper;
import org.springframework.ai.tool.ToolCallback;

import java.util.Map;

public class AiToolInvoker {

    private final AiToolRegistry registry;
    private final JsonMapper jsonMapper;

    public AiToolInvoker(AiToolRegistry registry, JsonMapper jsonMapper) {
        this.registry = registry;
        this.jsonMapper = jsonMapper;
    }

    public String invoke(String name, Map<String, Object> input) {
        ToolCallback callback = registry.get(name);
        if (callback == null) {
            throw new IllegalArgumentException("Unknown AI tool: " + name);
        }
        return callback.call(jsonMapper.writeValueAsString(input == null ? Map.of() : input));
    }
}
