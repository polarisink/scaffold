package com.scaffold.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "scaffold.ai")
public class ScaffoldAiProperties {

    private final Security security = new Security();
    private boolean enabled = true;
    private String systemPrompt = "You are Scaffold Assistant. Answer clearly and use available tools when useful.";
    private int memoryMaxMessages = 20;
    private String defaultConversationId = "default";
    private boolean advisorLoggingEnabled = true;
    private List<String> safeGuardWords = new ArrayList<>();

    @Data
    public static class Security {
        private boolean enabled;
        private String header = "X-AI-API-Key";
        private String apiKey = "";
    }
}
