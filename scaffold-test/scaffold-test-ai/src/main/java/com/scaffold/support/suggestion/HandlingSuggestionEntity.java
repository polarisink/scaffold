package com.scaffold.support.suggestion;

import com.scaffold.orm.BaseLongAuditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 综合处理建议持久化实体。
 */
@Getter
@Setter
@Entity
@Table(name = "ai_handling_suggestion")
public class HandlingSuggestionEntity extends BaseLongAuditable {

    /**
     * 建议所属工单。
     */
    @Column(nullable = false)
    private Long workOrderId;

    /**
     * 模型生成并经过结构化校验的问题诊断。
     */
    @Column(nullable = false, columnDefinition = "text")
    private String diagnosis;

    /**
     * 按执行顺序保存的推荐处理步骤。
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ai_handling_suggestion_action",
            joinColumns = @JoinColumn(name = "suggestion_id"))
    @OrderColumn(name = "action_order")
    // 集合元素没有可供命名策略推导的实体字段列，因此必须指定 action。
    @Column(name = "action", nullable = false, length = 1_000)
    private List<String> recommendedActions = new ArrayList<>();

    /**
     * 序列化后的知识引用来源。
     */
    @Column(nullable = false, columnDefinition = "text")
    private String sourcesJson;

    /**
     * 序列化后的工单、订单和知识证据链。
     */
    @Column(nullable = false, columnDefinition = "text")
    private String evidenceJson;

    /**
     * Java 规则修正后的最终风险等级。
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private RiskLevel riskLevel;

    /**
     * 是否必须由人工审核后再继续处理。
     */
    @Column(nullable = false)
    private Boolean manualReviewRequired;
}
