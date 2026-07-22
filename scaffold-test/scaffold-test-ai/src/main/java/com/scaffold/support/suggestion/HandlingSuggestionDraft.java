package com.scaffold.support.suggestion;

import java.util.List;

/**
 * 模型生成的结构化处理建议草稿，最终风险仍由服务端规则兜底。
 */
public record HandlingSuggestionDraft(String diagnosis, List<String> recommendedActions,
                                      RiskLevel riskLevel, boolean manualReviewRequired) {

    public HandlingSuggestionDraft {
        if (diagnosis == null || diagnosis.isBlank()) {
            throw new IllegalArgumentException("diagnosis must not be blank");
        }
        if (recommendedActions == null || recommendedActions.isEmpty()
                || recommendedActions.stream().anyMatch(action -> action == null || action.isBlank())) {
            throw new IllegalArgumentException("recommendedActions must not be empty");
        }
        recommendedActions = List.copyOf(recommendedActions);
        if (riskLevel == null) {
            throw new IllegalArgumentException("riskLevel must not be null");
        }
    }
}
