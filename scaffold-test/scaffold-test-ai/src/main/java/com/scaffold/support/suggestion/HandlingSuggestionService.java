package com.scaffold.support.suggestion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaffold.ai.chat.AiChatService;
import com.scaffold.ai.prompt.AiPromptTemplate;
import com.scaffold.ai.prompt.RenderedAiPrompt;
import com.scaffold.support.knowledge.KnowledgeDocument;
import com.scaffold.support.knowledge.api.KnowledgeSource;
import com.scaffold.support.knowledge.retrieval.KnowledgeRetriever;
import com.scaffold.support.order.OrderService;
import com.scaffold.support.order.model.OrderSummary;
import com.scaffold.support.workorder.OrderNotAccessibleException;
import com.scaffold.support.workorder.WorkOrder;
import com.scaffold.support.workorder.WorkOrderCategory;
import com.scaffold.support.workorder.WorkOrderService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZoneOffset;
import java.util.*;

/**
 * 汇总工单、订单和知识库事实，生成只读的综合处理建议。
 */
@Service
public class HandlingSuggestionService {

    private static final int KNOWLEDGE_LIMIT = 5;

    private final AiChatService aiChatService;
    private final AiPromptTemplate promptTemplate;
    private final WorkOrderService workOrderService;
    private final OrderService orderService;
    private final KnowledgeRetriever knowledgeRetriever;
    private final HandlingSuggestionRepository repository;
    private final ObjectMapper objectMapper;

    public HandlingSuggestionService(AiChatService aiChatService,
                                     @Qualifier("handlingSuggestionPrompt") AiPromptTemplate promptTemplate,
                                     WorkOrderService workOrderService, OrderService orderService,
                                     KnowledgeRetriever knowledgeRetriever, HandlingSuggestionRepository repository,
                                     ObjectMapper objectMapper) {
        this.aiChatService = aiChatService;
        this.promptTemplate = promptTemplate;
        this.workOrderService = workOrderService;
        this.orderService = orderService;
        this.knowledgeRetriever = knowledgeRetriever;
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    /**
     * 生成综合处理建议并持久化，整个过程不会修改工单或订单状态。
     */
    @Transactional
    public HandlingSuggestion generate(long workOrderId) {
        WorkOrder workOrder = workOrderService.getCurrentUserWorkOrder(workOrderId);
        Optional<OrderSummary> order = queryOrder(workOrder);
        List<KnowledgeDocument> documents = knowledgeRetriever.search(buildKnowledgeQuery(workOrder), KNOWLEDGE_LIMIT);
        List<KnowledgeSource> sources = documents.stream().map(this::toSource).toList();
        List<HandlingEvidence> evidence = buildEvidence(workOrder, order, documents);

        RenderedAiPrompt prompt = promptTemplate.render(Map.of(
                "workOrder", describe(workOrder),
                "order", order.map(this::describe).orElse("未提供或未查询到可访问的订单"),
                "knowledge", describeKnowledge(documents)));
        HandlingSuggestionDraft draft = aiChatService.entity(
                "handling-suggestion:" + workOrder.id() + ":" + UUID.randomUUID(),
                prompt, HandlingSuggestionDraft.class);
        boolean insufficientInformation = order.isEmpty() && documents.isEmpty();
        boolean highRisk = workOrder.manualReviewRequired() || workOrder.priority() >= 4
                || workOrder.category() == WorkOrderCategory.REFUND
                || workOrder.category() == WorkOrderCategory.COMPLAINT;
        RiskLevel riskLevel = draft.riskLevel();
        if (insufficientInformation) {
            riskLevel = RiskLevel.max(riskLevel, RiskLevel.MEDIUM);
        }
        if (highRisk) {
            riskLevel = RiskLevel.max(riskLevel, RiskLevel.HIGH);
        }

        HandlingSuggestionEntity entity = new HandlingSuggestionEntity();
        entity.setWorkOrderId(workOrder.id());
        entity.setDiagnosis(draft.diagnosis());
        entity.setRecommendedActions(draft.recommendedActions());
        entity.setSourcesJson(writeJson(sources));
        entity.setEvidenceJson(writeJson(evidence));
        entity.setRiskLevel(riskLevel);
        entity.setManualReviewRequired(draft.manualReviewRequired() || insufficientInformation || highRisk);
        return toDomain(repository.saveAndFlush(entity));
    }

    /**
     * 查询当前用户工单最近一次生成的综合处理建议。
     */
    @Transactional(readOnly = true)
    public HandlingSuggestion latest(long workOrderId) {
        WorkOrder workOrder = workOrderService.getCurrentUserWorkOrder(workOrderId);
        return repository.findTopByWorkOrderIdOrderByGmtCreatedDescIdDesc(workOrder.id())
                .map(this::toDomain)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "尚未生成处理建议"));
    }

    private Optional<OrderSummary> queryOrder(WorkOrder workOrder) {
        if (workOrder.orderNo() == null || workOrder.orderNo().isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(orderService.queryOrder(workOrder.orderNo(), workOrder.userId()));
        } catch (OrderNotAccessibleException ignored) {
            return Optional.empty();
        }
    }

    private String buildKnowledgeQuery(WorkOrder workOrder) {
        return workOrder.originalDescription() + "\n" + workOrder.summary();
    }

    private KnowledgeSource toSource(KnowledgeDocument document) {
        return new KnowledgeSource(document.documentId(), document.title(), document.version(), document.updatedAt());
    }

    private List<HandlingEvidence> buildEvidence(WorkOrder workOrder, Optional<OrderSummary> order,
                                                 List<KnowledgeDocument> documents) {
        List<HandlingEvidence> evidence = new ArrayList<>();
        evidence.add(new HandlingEvidence(EvidenceType.WORK_ORDER, "work-order:" + workOrder.id(),
                describe(workOrder)));
        order.ifPresent(summary -> evidence.add(new HandlingEvidence(EvidenceType.ORDER,
                "order:" + summary.orderNo(), describe(summary))));
        documents.forEach(document -> evidence.add(new HandlingEvidence(EvidenceType.KNOWLEDGE,
                "knowledge:" + document.documentId(), document.title() + "（版本 " + document.version() + "）")));
        return List.copyOf(evidence);
    }

    private String describe(WorkOrder workOrder) {
        return "工单号=" + workOrder.id() + "，类别=" + workOrder.category() + "，优先级="
                + workOrder.priority() + "，摘要=" + workOrder.summary() + "，用户描述="
                + workOrder.originalDescription();
    }

    private String describe(OrderSummary order) {
        return "订单号=" + order.orderNo() + "，商品=" + order.productName() + "，实付="
                + order.paidAmount() + "，订单状态=" + order.orderStatus() + "，售后状态="
                + order.afterSaleStatus();
    }

    private String describeKnowledge(List<KnowledgeDocument> documents) {
        if (documents.isEmpty()) {
            return "未检索到相关知识";
        }
        return documents.stream().map(document -> "[" + document.documentId() + "] " + document.title()
                + "（版本 " + document.version() + "）：" + document.content()).reduce((a, b) -> a + "\n" + b).orElseThrow();
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("无法序列化处理建议证据", exception);
        }
    }

    private HandlingSuggestion toDomain(HandlingSuggestionEntity entity) {
        try {
            List<KnowledgeSource> sources = objectMapper.readValue(entity.getSourcesJson(), new TypeReference<>() {
            });
            List<HandlingEvidence> evidence = objectMapper.readValue(entity.getEvidenceJson(), new TypeReference<>() {
            });
            return new HandlingSuggestion(entity.getId(), entity.getWorkOrderId(), entity.getDiagnosis(),
                    List.copyOf(entity.getRecommendedActions()), sources, evidence, entity.getRiskLevel(),
                    entity.getManualReviewRequired(), entity.getGmtCreated().toInstant(ZoneOffset.UTC));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("无法读取处理建议证据", exception);
        }
    }
}
