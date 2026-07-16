package com.scaffold.ai.controller;

import com.scaffold.ai.tool.AiToolInvoker;
import com.scaffold.ai.tool.AiToolRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai/tools")
public class AiToolController {

    private final AiToolRegistry registry;
    private final AiToolInvoker invoker;

    public AiToolController(AiToolRegistry registry, AiToolInvoker invoker) {
        this.registry = registry;
        this.invoker = invoker;
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        return registry.describeAll();
    }

    @PostMapping("/{name}/invoke")
    public Map<String, String> invoke(@PathVariable String name, @RequestBody(required = false) Map<String, Object> input) {
        return Map.of("result", invoker.invoke(name, input));
    }
}
