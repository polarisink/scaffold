package com.scaffold.support.knowledge.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record KnowledgeRequest(@Positive long workOrderId,
                               @NotBlank @Size(max = 4_000) String question) {
}
