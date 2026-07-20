package com.scaffold.support;

import com.scaffold.support.model.WorkOrder;
import com.scaffold.support.model.WorkOrderCategory;
import com.scaffold.support.model.WorkOrderIntent;
import com.scaffold.support.model.WorkOrderStatus;
import com.scaffold.support.repository.WorkOrderEntity;
import com.scaffold.support.repository.WorkOrderRepository;
import com.scaffold.support.security.SupportCurrentUserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WorkOrderServiceTest {

    private static final Instant NOW = Instant.parse("2026-07-19T03:00:00Z");

    private SupportIntentService intentService;
    private WorkOrderRepository repository;
    private AtomicLong currentUserId;
    private ChatMemory chatMemory;
    private WorkOrderService service;

    @BeforeEach
    void setUp() {
        intentService = mock(SupportIntentService.class);
        repository = mock(WorkOrderRepository.class);
        currentUserId = new AtomicLong(1_001L);
        chatMemory = mock(ChatMemory.class);
        SupportCurrentUserProvider currentUserProvider = currentUserId::get;
        service = new WorkOrderService(intentService, repository,
                currentUserProvider, Clock.fixed(NOW, ZoneOffset.UTC), chatMemory);
    }

    @Test
    void createsWorkOrderWithServerControlledFields() {
        when(intentService.analyze(anyString(), anyString())).thenReturn(new WorkOrderIntent(
                WorkOrderCategory.REFUND, "手机无法开机，用户申请退款", 4,
                "202607190001", true));
        when(repository.findByUserIdAndRequestIdAndDeleted(1_001L, "request_0001", 0))
                .thenReturn(Optional.empty());
        when(repository.saveAndFlush(any(WorkOrderEntity.class))).thenAnswer(invocation -> {
            WorkOrderEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        WorkOrder workOrder = service.create("request_0001",
                "手机无法开机，订单号202607190001，我要退款");

        assertThat(workOrder.id()).isEqualTo(1L);
        assertThat(workOrder.userId()).isEqualTo(1_001L);
        assertThat(workOrder.conversationId()).startsWith("work-order:");
        assertThat(workOrder.status()).isEqualTo(WorkOrderStatus.MANUAL_REVIEW);
        assertThat(workOrder.createdAt()).isEqualTo(NOW);
        ArgumentCaptor<WorkOrderEntity> captor = ArgumentCaptor.forClass(WorkOrderEntity.class);
        verify(repository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getCreatedBy()).isEqualTo(1_001L);
        assertThat(captor.getValue().getOriginalDescription()).contains("202607190001");
    }

    @Test
    void returnsPersistedWorkOrderForRepeatedRequest() {
        WorkOrderEntity persisted = persistedEntity(1L, 1_001L, "request_0002");
        when(repository.findByUserIdAndRequestIdAndDeleted(1_001L, "request_0002", 0))
                .thenReturn(Optional.of(persisted));

        WorkOrder repeated = service.create("request_0002", "重复提交时不会再次分析");

        assertThat(repeated.id()).isEqualTo(1L);
        verify(intentService, never()).analyze(anyString(), anyString());
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void listsAndGetsOnlyCurrentUsersWorkOrders() {
        WorkOrderEntity persisted = persistedEntity(1L, 1_001L, "request_0003");
        when(repository.findByUserIdAndDeletedOrderByGmtCreatedDescIdDesc(1_001L, 0))
                .thenReturn(List.of(persisted));
        when(repository.findByIdAndUserIdAndDeleted(1L, 1_001L, 0)).thenReturn(Optional.of(persisted));

        assertThat(service.listCurrentUserWorkOrders()).extracting(WorkOrder::id).containsExactly(1L);
        assertThat(service.getCurrentUserWorkOrder(1L).userId()).isEqualTo(1_001L);

        currentUserId.set(2_002L);
        when(repository.findByUserIdAndDeletedOrderByGmtCreatedDescIdDesc(2_002L, 0)).thenReturn(List.of());
        when(repository.findByIdAndUserIdAndDeleted(1L, 2_002L, 0)).thenReturn(Optional.empty());
        assertThat(service.listCurrentUserWorkOrders()).isEmpty();
        assertThatThrownBy(() -> service.getCurrentUserWorkOrder(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("工单不存在");
    }

    @Test
    void rejectsInvalidCreateRequestBeforeCallingModelOrDatabase() {
        assertThatThrownBy(() -> service.create("bad", "正常描述"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("requestId");
        assertThatThrownBy(() -> service.create("request_0004", "  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("description");
        verify(intentService, times(0)).analyze(anyString(), anyString());
        verify(repository, never()).findByUserIdAndRequestIdAndDeleted(anyLong(), anyString(), eq(0));
    }

    @Test
    void closesOwnWorkOrderAndClearsConversationMemory() {
        WorkOrderEntity persisted = persistedEntity(1L, 1_001L, "request_0005");
        when(repository.findByIdAndUserIdAndDeleted(1L, 1_001L, 0)).thenReturn(Optional.of(persisted));
        when(repository.saveAndFlush(persisted)).thenReturn(persisted);

        WorkOrder closed = service.close(1L);

        assertThat(closed.status()).isEqualTo(WorkOrderStatus.CLOSED);
        verify(chatMemory).clear("work-order:test:1");
        verify(repository).saveAndFlush(persisted);
    }

    private WorkOrderEntity persistedEntity(long id, long userId, String requestId) {
        WorkOrderEntity entity = new WorkOrderEntity();
        entity.setId(id);
        entity.setUserId(userId);
        entity.setRequestId(requestId);
        entity.setConversationId("work-order:test:" + id);
        entity.setCategory(WorkOrderCategory.ORDER_QUERY);
        entity.setSummary("查询订单状态");
        entity.setPriority(2);
        entity.setStatus(WorkOrderStatus.OPEN);
        entity.setOrderNo("202607190001");
        entity.setManualReviewRequired(false);
        entity.setOriginalDescription("查询订单状态");
        entity.setGmtCreated(LocalDateTime.ofInstant(NOW, ZoneOffset.UTC));
        entity.setGmtModified(entity.getGmtCreated());
        entity.setCreatedBy(userId);
        entity.setModifiedBy(userId);
        return entity;
    }
}
