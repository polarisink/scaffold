package com.scaffold.ai.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.ai.tool.support.ToolDefinitions;
import org.springframework.ai.tool.support.ToolUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AiToolRegistry implements BeanPostProcessor, ToolCallbackProvider {

    private static final Logger log = LoggerFactory.getLogger(AiToolRegistry.class);
    private final Map<String, ToolCallback> callbacks = new ConcurrentHashMap<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithMethods(ClassUtils.getUserClass(bean), method -> {
            if (AnnotationUtils.findAnnotation(method, Tool.class) != null) register(bean, method);
        });
        return bean;
    }

    private void register(Object bean, Method method) {
        String name = ToolUtils.getToolName(method);
        ToolCallback callback = MethodToolCallback.builder()
                .toolDefinition(ToolDefinitions.from(method))
                .toolMethod(method).toolObject(bean).build();
        if (callbacks.putIfAbsent(name, callback) == null) {
            log.info("[scaffold-ai] Registered tool: {}", name);
        } else {
            log.warn("[scaffold-ai] Duplicate tool ignored: {}", name);
        }
    }

    @Override
    public ToolCallback[] getToolCallbacks() { return callbacks.values().toArray(ToolCallback[]::new); }
    public Collection<ToolCallback> listAll() { return Collections.unmodifiableCollection(callbacks.values()); }
    public ToolCallback get(String name) { return callbacks.get(name); }
    public int size() { return callbacks.size(); }

    public List<Map<String, Object>> describeAll() {
        List<Map<String, Object>> result = new ArrayList<>();
        callbacks.values().stream().map(ToolCallback::getToolDefinition)
                .forEach(def -> result.add(Map.of("name", def.name(), "description", def.description(),
                        "inputSchema", def.inputSchema())));
        return result;
    }
}
