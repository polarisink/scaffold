package com.scaffold.ai.chat;

import com.scaffold.ai.config.ScaffoldAiProperties;
import com.scaffold.ai.prompt.RenderedAiPrompt;
import com.scaffold.ai.tool.AiToolRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AiChatService {

    private final ChatClient chatClient;
    private final ScaffoldAiProperties properties;

    public AiChatService(ChatClient.Builder builder, AiToolRegistry registry,
                         ChatMemory memory, ScaffoldAiProperties properties) {
        this.properties = properties;
        List<Advisor> advisors = new ArrayList<>();
        advisors.add(MessageChatMemoryAdvisor.builder(memory).build());
        if (properties.isAdvisorLoggingEnabled()) {
            advisors.add(new SimpleLoggerAdvisor());
        }
        if (!properties.getSafeGuardWords().isEmpty()) {
            advisors.add(SafeGuardAdvisor.builder().sensitiveWords(properties.getSafeGuardWords()).build());
        }
        this.chatClient = builder.defaultSystem(properties.getSystemPrompt())
                .defaultAdvisors(advisors.toArray(Advisor[]::new))
                .defaultToolCallbacks(registry)
                .build();
    }

    public String chat(String conversationId, String systemPrompt, String message) {
        return request(conversationId, systemPrompt).user(message).call().content();
    }

    /**
     * Runs a chat request with an explicit, least-privilege tool allow-list and trusted
     * server-side context. Values in {@code toolContext} are not part of the JSON schema
     * presented to the model.
     */
    public String chat(String conversationId, String systemPrompt, String message,
                       Map<String, Object> toolContext, String... toolNames) {
        ChatClient.ChatClientRequestSpec request = request(conversationId, systemPrompt)
                .toolContext(toolContext == null ? Map.of() : Map.copyOf(toolContext));
        if (toolNames != null && toolNames.length > 0) {
            request = request.toolNames(toolNames);
        }
        return request.user(message).call().content();
    }

    public Flux<String> stream(String conversationId, String systemPrompt, String message) {
        return request(conversationId, systemPrompt).user(message).stream().content();
    }

    public <T> T entity(String conversationId, String systemPrompt, String message, Class<T> type) {
        return request(conversationId, systemPrompt).user(message).call().entity(type);
    }

    public <T> T entity(String conversationId, RenderedAiPrompt prompt, Class<T> type) {
        return entity(conversationId, prompt.system(), prompt.user(), type);
    }

    private ChatClient.ChatClientRequestSpec request(String conversationId, String systemPrompt) {
        String id = conversationId == null || conversationId.isBlank()
                ? properties.getDefaultConversationId() : conversationId;
        ChatClient.ChatClientRequestSpec request = chatClient.prompt()
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, id));
        return systemPrompt == null || systemPrompt.isBlank() ? request : request.system(systemPrompt);
    }
}
