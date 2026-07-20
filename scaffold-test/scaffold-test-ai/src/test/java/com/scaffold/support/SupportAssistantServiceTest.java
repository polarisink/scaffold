package com.scaffold.support;

import com.scaffold.ai.chat.AiChatService;
import com.scaffold.support.repository.WorkOrderEntity;
import com.scaffold.support.repository.WorkOrderRepository;
import com.scaffold.support.model.WorkOrderStatus;
import com.scaffold.support.security.SupportCurrentUserProvider;
import com.scaffold.support.tool.SupportToolContext;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SupportAssistantServiceTest {

    @Test
    void suppliesTrustedIdentityAndOnlyReadOnlySupportTools() {
        AiChatService chatService = mock(AiChatService.class);
        when(chatService.chat(anyString(), anyString(), anyString(), any(), any(String[].class)))
                .thenReturn("订单已签收");
        WorkOrderRepository repository = mock(WorkOrderRepository.class);
        WorkOrderEntity workOrder = new WorkOrderEntity();
        workOrder.setConversationId("server-conversation-1");
        when(repository.findByIdAndUserIdAndDeleted(42L, 1_001L, 0)).thenReturn(Optional.of(workOrder));
        SupportCurrentUserProvider userProvider = () -> 1_001L;
        SupportAssistantService service = new SupportAssistantService(chatService, userProvider, repository);

        String result = service.chat(42L, "查询订单202607190001的物流");

        assertThat(result).isEqualTo("订单已签收");
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> contextCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<String[]> toolsCaptor = ArgumentCaptor.forClass(String[].class);
        verify(chatService).chat(eq("server-conversation-1"), anyString(), eq("查询订单202607190001的物流"),
                contextCaptor.capture(), toolsCaptor.capture());
        assertThat(contextCaptor.getValue())
                .containsEntry(SupportToolContext.USER_ID, 1_001L)
                .containsKey(SupportToolContext.REQUEST_ID);
        assertThat(toolsCaptor.getValue()).containsExactly(
                "query_order", "query_logistics", "query_product");
    }

    @Test
    void rejectsBlankAndOversizedMessagesBeforeCallingTheModel() {
        WorkOrderRepository repository = mock(WorkOrderRepository.class);
        WorkOrderEntity workOrder = new WorkOrderEntity();
        workOrder.setConversationId("server-conversation-1");
        when(repository.findByIdAndUserIdAndDeleted(1L, 1_001L, 0)).thenReturn(Optional.of(workOrder));
        SupportAssistantService service = new SupportAssistantService(
                mock(AiChatService.class), () -> 1_001L, repository);

        assertThatThrownBy(() -> service.chat(1L, " "))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.chat(1L, "x".repeat(4_001)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsConversationAccessWhenWorkOrderBelongsToAnotherUser() {
        AiChatService chatService = mock(AiChatService.class);
        WorkOrderRepository repository = mock(WorkOrderRepository.class);
        when(repository.findByIdAndUserIdAndDeleted(42L, 2_002L, 0)).thenReturn(Optional.empty());
        SupportAssistantService service = new SupportAssistantService(chatService, () -> 2_002L, repository);

        assertThatThrownBy(() -> service.chat(42L, "继续处理这个工单"))
                .isInstanceOf(com.scaffold.support.order.OrderNotAccessibleException.class);
        verifyNoInteractions(chatService);
    }

    @Test
    void rejectsMessagesAfterWorkOrderIsClosed() {
        AiChatService chatService = mock(AiChatService.class);
        WorkOrderRepository repository = mock(WorkOrderRepository.class);
        WorkOrderEntity workOrder = new WorkOrderEntity();
        workOrder.setConversationId("closed-conversation");
        workOrder.setStatus(WorkOrderStatus.CLOSED);
        when(repository.findByIdAndUserIdAndDeleted(42L, 1_001L, 0)).thenReturn(Optional.of(workOrder));
        SupportAssistantService service = new SupportAssistantService(chatService, () -> 1_001L, repository);

        assertThatThrownBy(() -> service.chat(42L, "再问一个问题"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("closed");
        verifyNoInteractions(chatService);
    }
}
