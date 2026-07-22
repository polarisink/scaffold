package com.scaffold.support.intent;

import com.scaffold.ai.prompt.AiPromptMetadata;
import com.scaffold.ai.prompt.AiPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * 加载并注册阶段一版本化 Prompt 模板。
 */
@Configuration(proxyBeanMethods = false)
public class SupportPromptConfiguration {

    /**
     * 创建工单意图识别 v1 Prompt。
     */
    @Bean
    AiPromptTemplate workOrderIntentPrompt(
            @Value("classpath:/prompts/support/intent/v1/system.st") Resource system,
            @Value("classpath:/prompts/support/intent/v1/user.st") Resource user) {
        return AiPromptTemplate.from(new AiPromptMetadata("support-intent", "v1"), system, user);
    }
}
