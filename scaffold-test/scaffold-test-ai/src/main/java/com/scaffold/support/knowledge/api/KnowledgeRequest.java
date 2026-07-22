package com.scaffold.support.knowledge.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * 针对当前用户工单发起的知识库问答请求。
 */
public record KnowledgeRequest(@Positive long workOrderId,
                               @NotBlank @Size(max = 4_000) String question) {
}
