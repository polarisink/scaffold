package com.scaffold.ai.prompt;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Versioned system/user prompt template loaded once from Spring resources.
 * Spring AI's {@link PromptTemplate} performs variable substitution per request.
 */
public final class AiPromptTemplate {

    private final AiPromptMetadata metadata;
    private final String systemTemplate;
    private final String userTemplate;

    private AiPromptTemplate(AiPromptMetadata metadata, String systemTemplate, String userTemplate) {
        this.metadata = metadata;
        this.systemTemplate = systemTemplate;
        this.userTemplate = userTemplate;
    }

    public static AiPromptTemplate from(AiPromptMetadata metadata, Resource system, Resource user) {
        Assert.notNull(metadata, "Prompt metadata must not be null");
        return new AiPromptTemplate(metadata, read(system, "system"), read(user, "user"));
    }

    public RenderedAiPrompt render(Map<String, Object> userVariables) {
        return render(Map.of(), userVariables);
    }

    public RenderedAiPrompt render(Map<String, Object> systemVariables,
                                   Map<String, Object> userVariables) {
        Assert.notNull(systemVariables, "System variables must not be null");
        Assert.notNull(userVariables, "User variables must not be null");
        String system = new PromptTemplate(systemTemplate).render(systemVariables);
        String user = new PromptTemplate(userTemplate).render(userVariables);
        return new RenderedAiPrompt(metadata, system, user);
    }

    public AiPromptMetadata metadata() {
        return metadata;
    }

    private static String read(Resource resource, String role) {
        Assert.notNull(resource, role + " prompt resource must not be null");
        try {
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read " + role + " prompt from " + resource, ex);
        }
    }
}
