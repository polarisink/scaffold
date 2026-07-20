package com.scaffold.ai.prompt;

import org.springframework.util.Assert;

/** Identifies the prompt used for an AI call so it can be logged and evaluated. */
public record AiPromptMetadata(String name, String version) {

    public AiPromptMetadata {
        Assert.hasText(name, "Prompt name must not be blank");
        Assert.hasText(version, "Prompt version must not be blank");
    }
}
