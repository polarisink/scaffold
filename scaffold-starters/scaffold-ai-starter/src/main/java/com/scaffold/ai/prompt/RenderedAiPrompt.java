package com.scaffold.ai.prompt;

import org.springframework.util.Assert;

/** A fully rendered system/user prompt pair ready to be sent to a model. */
public record RenderedAiPrompt(AiPromptMetadata metadata, String system, String user) {

    public RenderedAiPrompt {
        Assert.notNull(metadata, "Prompt metadata must not be null");
        Assert.hasText(system, "System prompt must not be blank");
        Assert.hasText(user, "User prompt must not be blank");
    }
}
