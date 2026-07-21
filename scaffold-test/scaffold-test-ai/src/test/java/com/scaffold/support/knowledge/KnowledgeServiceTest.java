package com.scaffold.support.knowledge;

import com.scaffold.ai.chat.AiChatService;
import com.scaffold.support.knowledge.api.KnowledgeAnswer;
import com.scaffold.support.knowledge.api.KnowledgeRequest;
import com.scaffold.support.knowledge.api.KnowledgeSource;
import com.scaffold.support.knowledge.retrieval.KnowledgeRetriever;
import com.scaffold.support.workorder.WorkOrderEntity;
import com.scaffold.support.workorder.WorkOrderRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class KnowledgeServiceTest {

    @Test
    void answersWithVersionedSources() {
        AiChatService chat = mock(AiChatService.class);
        when(chat.chat(anyString(), anyString(), anyString(), any(), any(String[].class)))
                .thenReturn("质量问题在签收后七日内可以申请退款。");
        WorkOrderRepository workOrders = mock(WorkOrderRepository.class);
        when(workOrders.findByIdAndUserIdAndDeleted(42L, 1001L, 0)).thenReturn(Optional.of(new WorkOrderEntity()));
        KnowledgeDocument refund = new KnowledgeDocument("refund-policy", "退款政策", "2.0",
                Instant.parse("2026-07-21T00:00:00Z"), "质量问题签收后七日内可以申请退款");
        KnowledgeRetriever retriever = mock(KnowledgeRetriever.class);
        when(retriever.search("质量问题如何退款", 3)).thenReturn(List.of(refund));
        KnowledgeService service = new KnowledgeService(retriever, chat, workOrders, () -> 1001L);

        KnowledgeAnswer grounded = service.answer(new KnowledgeRequest(42L, "质量问题如何退款"));
        assertThat(grounded.grounded()).isTrue();
        assertThat(grounded.sources()).extracting(KnowledgeSource::documentId).containsExactly("refund-policy");

    }

    @Test
    void doesNotCallModelWhenNothingWasRetrieved() {
        AiChatService chat = mock(AiChatService.class);
        WorkOrderRepository workOrders = mock(WorkOrderRepository.class);
        when(workOrders.findByIdAndUserIdAndDeleted(42L, 1001L, 0)).thenReturn(Optional.of(new WorkOrderEntity()));
        KnowledgeRetriever retriever = mock(KnowledgeRetriever.class);
        when(retriever.search("火星维修政策", 3)).thenReturn(List.of());
        KnowledgeService service = new KnowledgeService(retriever, chat, workOrders, () -> 1001L);

        assertThat(service.answer(new KnowledgeRequest(42L, "火星维修政策")).grounded()).isFalse();
        verifyNoInteractions(chat);
    }
}
