package com.scaffold.ai.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaffold.ai.chat.AiChatService;
import com.scaffold.ai.controller.AiChatController;
import com.scaffold.ai.controller.AiToolController;
import com.scaffold.ai.tool.AiToolInvoker;
import com.scaffold.ai.tool.AiToolRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration(afterName = "org.springframework.ai.model.chat.client.autoconfigure.ChatClientAutoConfiguration")
@ConditionalOnClass(ChatClient.class)
@ConditionalOnProperty(prefix = "scaffold.ai", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(ScaffoldAiProperties.class)
public class ScaffoldAiAutoConfiguration implements WebMvcConfigurer {

    private final ScaffoldAiProperties properties;

    public ScaffoldAiAutoConfiguration(ScaffoldAiProperties properties) { this.properties = properties; }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (properties.getSecurity().isEnabled()) {
            registry.addInterceptor(new AiApiKeyInterceptor(properties.getSecurity())).addPathPatterns("/api/ai/**");
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public static AiToolRegistry aiToolRegistry() { return new AiToolRegistry(); }

    @Bean
    @ConditionalOnMissingBean
    public AiToolInvoker aiToolInvoker(AiToolRegistry registry, ObjectMapper mapper) {
        return new AiToolInvoker(registry, mapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public ChatMemory chatMemory(ScaffoldAiProperties properties) {
        return MessageWindowChatMemory.builder().chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(properties.getMemoryMaxMessages()).build();
    }

    @Bean
    @ConditionalOnBean(ChatClient.Builder.class)
    @ConditionalOnMissingBean
    public AiChatService aiChatService(ChatClient.Builder builder, AiToolRegistry registry,
                                       ChatMemory memory, ScaffoldAiProperties properties) {
        return new AiChatService(builder, registry, memory, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public AiToolController aiToolController(AiToolRegistry registry, AiToolInvoker invoker) {
        return new AiToolController(registry, invoker);
    }

    @Bean
    @ConditionalOnBean(AiChatService.class)
    @ConditionalOnMissingBean
    public AiChatController aiChatController(AiChatService service, ScaffoldAiProperties properties) {
        return new AiChatController(service, properties);
    }
}
