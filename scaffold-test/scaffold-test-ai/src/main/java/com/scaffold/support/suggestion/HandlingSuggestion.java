package com.scaffold.support.suggestion;

import com.scaffold.support.knowledge.api.KnowledgeSource;

import java.time.Instant;
import java.util.List;

/**
 * 已持久化的综合处理建议及其完整证据链。
 */
public record HandlingSuggestion(Long id, long workOrderId, String diagnosis,
                                 List<String> recommendedActions, List<KnowledgeSource> sources,
                                 List<HandlingEvidence> evidence, RiskLevel riskLevel,
                                 boolean manualReviewRequired, Instant generatedAt) {
}
