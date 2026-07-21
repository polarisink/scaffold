package com.scaffold.support.intent;

import com.scaffold.ai.chat.AiChatService;
import com.scaffold.ai.prompt.AiPromptTemplate;
import com.scaffold.ai.prompt.RenderedAiPrompt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 使用结构化输出将售后描述解析为工单意图，模型只能填写 {@link WorkOrderIntent} 定义的字段。
 */
@Service
@RequiredArgsConstructor
public class SupportIntentService {

    private final AiChatService aiChatService;
    private final AiPromptTemplate promptTemplate;

    /** 分析一段售后描述并返回经过类型约束的工单意图。 */
    public WorkOrderIntent analyze(AnalyzeRequest request) {
        RenderedAiPrompt prompt = promptTemplate.render(Map.of("message", request.message()));
        return aiChatService.entity(request.conversationId(), prompt, WorkOrderIntent.class);
    }
}
