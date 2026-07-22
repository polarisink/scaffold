package com.scaffold.support.suggestion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaffold.ai.chat.AiChatService;
import com.scaffold.ai.prompt.AiPromptMetadata;
import com.scaffold.ai.prompt.AiPromptTemplate;
import com.scaffold.ai.prompt.RenderedAiPrompt;
import com.scaffold.support.knowledge.KnowledgeDocument;
import com.scaffold.support.knowledge.retrieval.KnowledgeRetriever;
import com.scaffold.support.order.OrderService;
import com.scaffold.support.order.model.OrderSummary;
import com.scaffold.support.workorder.WorkOrder;
import com.scaffold.support.workorder.WorkOrderCategory;
import com.scaffold.support.workorder.WorkOrderService;
import com.scaffold.support.workorder.WorkOrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** 验证综合建议的证据汇总、风险规则和持久化结果。 */
class HandlingSuggestionServiceTest {

    @Test
    void generatesAndPersistsSuggestionWithTraceableEvidence() {
        WorkOrderService workOrders = mock(WorkOrderService.class);
        OrderService orders = mock(OrderService.class);
        KnowledgeRetriever retriever = mock(KnowledgeRetriever.class);
        AiChatService chat = mock(AiChatService.class);
        HandlingSuggestionRepository repository = mock(HandlingSuggestionRepository.class);
        WorkOrder workOrder = workOrder(false, 3, "202607190001");
        when(workOrders.getCurrentUserWorkOrder(42L)).thenReturn(workOrder);
        when(orders.queryOrder("202607190001", 1001L)).thenReturn(new OrderSummary(
                "202607190001", "Scaffold Phone X", new BigDecimal("3999.00"), "DELIVERED", "NONE"));
        KnowledgeDocument policy = new KnowledgeDocument("refund-policy", "退款政策", "1.0",
                Instant.parse("2026-07-21T00:00:00Z"), "质量问题签收后七日内可以申请退款。");
        when(retriever.search(any(), eq(5))).thenReturn(List.of(policy));
        when(chat.entity(any(), any(RenderedAiPrompt.class), eq(HandlingSuggestionDraft.class)))
                .thenReturn(new HandlingSuggestionDraft("疑似商品质量问题", List.of("核对故障现象", "提交退款审核"),
                        RiskLevel.MEDIUM, false));
        when(repository.saveAndFlush(any())).thenAnswer(invocation -> {
            HandlingSuggestionEntity entity = invocation.getArgument(0);
            entity.setId(7L);
            entity.setGmtCreated(LocalDateTime.parse("2026-07-22T01:00:00"));
            return entity;
        });
        HandlingSuggestionService service = new HandlingSuggestionService(chat, prompt(), workOrders, orders,
                retriever, repository, new ObjectMapper().findAndRegisterModules());

        HandlingSuggestion suggestion = service.generate(42L);

        assertThat(suggestion.diagnosis()).isEqualTo("疑似商品质量问题");
        assertThat(suggestion.recommendedActions()).containsExactly("核对故障现象", "提交退款审核");
        assertThat(suggestion.sources()).extracting("documentId").containsExactly("refund-policy");
        assertThat(suggestion.evidence()).extracting(HandlingEvidence::type)
                .contains(EvidenceType.WORK_ORDER, EvidenceType.ORDER, EvidenceType.KNOWLEDGE);
        verify(repository).saveAndFlush(any(HandlingSuggestionEntity.class));
    }

    @Test
    void forcesManualReviewAndRaisesRiskWhenInformationIsInsufficient() {
        WorkOrderService workOrders = mock(WorkOrderService.class);
        OrderService orders = mock(OrderService.class);
        KnowledgeRetriever retriever = mock(KnowledgeRetriever.class);
        AiChatService chat = mock(AiChatService.class);
        HandlingSuggestionRepository repository = mock(HandlingSuggestionRepository.class);
        when(workOrders.getCurrentUserWorkOrder(42L)).thenReturn(workOrder(false, 2, null));
        when(retriever.search(any(), eq(5))).thenReturn(List.of());
        when(chat.entity(any(), any(RenderedAiPrompt.class), eq(HandlingSuggestionDraft.class)))
                .thenReturn(new HandlingSuggestionDraft("信息不足", List.of("补充订单号"), RiskLevel.LOW, false));
        when(repository.saveAndFlush(any())).thenAnswer(invocation -> {
            HandlingSuggestionEntity entity = invocation.getArgument(0);
            entity.setId(8L);
            entity.setGmtCreated(LocalDateTime.parse("2026-07-22T01:00:00"));
            return entity;
        });
        HandlingSuggestionService service = new HandlingSuggestionService(chat, prompt(), workOrders, orders,
                retriever, repository, new ObjectMapper().findAndRegisterModules());

        HandlingSuggestion suggestion = service.generate(42L);

        assertThat(suggestion.riskLevel()).isEqualTo(RiskLevel.MEDIUM);
        assertThat(suggestion.manualReviewRequired()).isTrue();
    }

    private AiPromptTemplate prompt() {
        return AiPromptTemplate.from(new AiPromptMetadata("handling-suggestion", "v1"),
                new ClassPathResource("prompts/support/suggestion/v1/system.st"),
                new ClassPathResource("prompts/support/suggestion/v1/user.st"));
    }

    private WorkOrder workOrder(boolean manualReview, int priority, String orderNo) {
        return new WorkOrder(42L, 1001L, "request_0042", "work-order:42", WorkOrderCategory.REPAIR,
                "手机无法开机", priority, WorkOrderStatus.OPEN, orderNo, manualReview,
                "手机无法开机，想申请售后", Instant.parse("2026-07-21T00:00:00Z"));
    }
}
