package com.scaffold.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * scaffold ai配置
 *
 * @param security              安全
 * @param enabled               是否启用
 * @param systemPrompt          系统prompt
 * @param memoryMaxMessages     内存最大信息
 * @param defaultConversationId 默认消息id
 * @param advisorLoggingEnabled
 * @param safeGuardWords
 */
@ConfigurationProperties(prefix = "scaffold.ai")
public record ScaffoldAiProperties(
        Security security,
        Boolean enabled,
        String systemPrompt,
        int memoryMaxMessages,
        String defaultConversationId,
        Boolean advisorLoggingEnabled,
        List<String> safeGuardWords
) {
    public ScaffoldAiProperties {
        if (security == null) {
            security = new Security();
        }
        if (enabled == null) {
            enabled = true;
        }
        if (systemPrompt == null) {
            systemPrompt = "You are Scaffold Assistant. Answer clearly and use available tools when useful.";
        }
        if (memoryMaxMessages <= 0) {
            memoryMaxMessages = 20;
        }
        if (defaultConversationId == null) {
            defaultConversationId = "default";
        }
        if (advisorLoggingEnabled == null) {
            advisorLoggingEnabled = true;
        }
        if (safeGuardWords == null) {
            safeGuardWords = new ArrayList<>();
        }
    }

    @Data
    public static class Security {
        private boolean enabled;
        private String header = "X-AI-API-Key";
        private String apiKey = "";
    }
}
