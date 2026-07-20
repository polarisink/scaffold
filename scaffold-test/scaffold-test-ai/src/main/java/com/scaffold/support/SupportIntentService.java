package com.scaffold.support;

import com.scaffold.ai.chat.AiChatService;
import com.scaffold.ai.prompt.AiPromptTemplate;
import com.scaffold.ai.prompt.RenderedAiPrompt;
import com.scaffold.support.model.WorkOrderIntent;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SupportIntentService {

    private static final int MAX_MESSAGE_LENGTH = 4_000;

    private final AiChatService aiChatService;
    private final AiPromptTemplate promptTemplate;

    public SupportIntentService(AiChatService aiChatService, AiPromptTemplate workOrderIntentPrompt) {
        this.aiChatService = aiChatService;
        this.promptTemplate = workOrderIntentPrompt;
    }

    public WorkOrderIntent analyze(String conversationId, String message) {
        String validatedMessage = validateMessage(message);
        RenderedAiPrompt prompt = promptTemplate.render(Map.of("message", validatedMessage));
        return aiChatService.entity(conversationId, prompt, WorkOrderIntent.class);
    }

    private String validateMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
        String normalized = message.trim();
        if (normalized.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("message must not exceed " + MAX_MESSAGE_LENGTH + " characters");
        }
        return normalized;
    }
}
