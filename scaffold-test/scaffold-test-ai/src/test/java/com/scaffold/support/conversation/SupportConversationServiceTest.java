package com.scaffold.support.conversation;

import com.scaffold.support.identity.SupportCurrentUserProvider;
import com.scaffold.support.workorder.WorkOrderEntity;
import com.scaffold.support.workorder.WorkOrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.memory.ChatMemory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class SupportConversationServiceTest {

    @Test
    void currentUserCanRestoreOrderedMessagesButCannotReadAnotherUsersConversation() {
        WorkOrderRepository workOrders = mock(WorkOrderRepository.class);
        SupportMessageRepository messages = mock(SupportMessageRepository.class);
        SupportCurrentUserProvider currentUser = () -> 1001L;
        WorkOrderEntity owned = new WorkOrderEntity();
        owned.setId(42L);
        when(workOrders.findByIdAndUserIdAndDeleted(42L, 1001L, 0)).thenReturn(Optional.of(owned));
        when(workOrders.findByIdAndUserIdAndDeleted(43L, 1001L, 0)).thenReturn(Optional.empty());
        when(messages.findTop20ByWorkOrderIdOrderBySequenceDesc(42L)).thenReturn(List.of(
                message(2, "ASSISTANT", "请提供订单号"), message(1, "USER", "手机坏了")));
        SupportConversationService service = new SupportConversationService(workOrders, messages, currentUser);

        assertThat(service.history(42L)).extracting(SupportMessageRes::content)
                .containsExactly("手机坏了", "请提供订单号");
        assertThatThrownBy(() -> service.history(43L)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void restoresTheModelWindowFromPersistentHistoryAfterRestart() {
        WorkOrderRepository workOrders = mock(WorkOrderRepository.class);
        SupportMessageRepository messages = mock(SupportMessageRepository.class);
        when(messages.findTop20ByWorkOrderIdOrderBySequenceDesc(42L)).thenReturn(List.of(
                message(2, "ASSISTANT", "请提供订单号"), message(1, "USER", "手机坏了")));
        ChatMemory memory = mock(ChatMemory.class);
        when(memory.get("work-order:42")).thenReturn(List.of());
        SupportConversationService service = new SupportConversationService(workOrders, messages, () -> 1001L);

        service.restoreMemory(42L, "work-order:42", memory);

        @SuppressWarnings("unchecked")
        org.mockito.ArgumentCaptor<List<org.springframework.ai.chat.messages.Message>> captor =
                org.mockito.ArgumentCaptor.forClass(List.class);
        verify(memory).add(org.mockito.ArgumentMatchers.eq("work-order:42"), captor.capture());
        assertThat(captor.getValue()).extracting(org.springframework.ai.chat.messages.Message::getText)
                .containsExactly("手机坏了", "请提供订单号");
    }

    private SupportMessageEntity message(long sequence, String role, String content) {
        SupportMessageEntity message = new SupportMessageEntity();
        message.setId(sequence);
        message.setSequence(sequence);
        message.setRole(role);
        message.setContent(content);
        message.setGmtCreated(LocalDateTime.of(2026, 7, 21, 10, 0).plusSeconds(sequence));
        return message;
    }
}
