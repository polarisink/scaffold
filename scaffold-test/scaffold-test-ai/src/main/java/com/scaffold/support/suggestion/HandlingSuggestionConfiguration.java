package com.scaffold.support.suggestion;

import com.scaffold.ai.prompt.AiPromptMetadata;
import com.scaffold.ai.prompt.AiPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * 阶段六处理建议所需的提示词配置。
 */
@Configuration(proxyBeanMethods = false)
public class HandlingSuggestionConfiguration {

    @Bean
    AiPromptTemplate handlingSuggestionPrompt(
            @Value("classpath:/prompts/support/suggestion/v1/system.st") Resource system,
            @Value("classpath:/prompts/support/suggestion/v1/user.st") Resource user) {
        return AiPromptTemplate.from(new AiPromptMetadata("handling-suggestion", "v1"), system, user);
    }
}
