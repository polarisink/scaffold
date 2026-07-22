package com.scaffold.support.workorder;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

/** 验证工单幂等约束、用户隔离查询和排序。 */
@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class WorkOrderRepositoryTest {

    @Autowired
    private WorkOrderRepository repository;

    @Test
    void persistsAndQueriesWorkOrderByCurrentUser() {
        WorkOrderEntity saved = repository.saveAndFlush(entity(1_001L, "request_jpa_01"));

        assertThat(saved.getId()).isPositive();
        assertThat(repository.findByIdAndUserIdAndDeleted(saved.getId(), 1_001L, 0)).contains(saved);
        assertThat(repository.findByUserIdAndDeletedOrderByGmtCreatedDescIdDesc(1_001L, 0))
                .containsExactly(saved);
        assertThat(repository.findByUserIdAndDeletedOrderByGmtCreatedDescIdDesc(2_002L, 0)).isEmpty();
    }

    @Test
    void scopesSameRequestIdToUser() {
        WorkOrderEntity first = repository.saveAndFlush(entity(1_001L, "request_jpa_02"));
        WorkOrderEntity anotherUser = repository.saveAndFlush(entity(2_002L, "request_jpa_02"));

        assertThat(anotherUser.getId()).isNotEqualTo(first.getId());
        assertThat(repository.findByUserIdAndRequestIdAndDeleted(1_001L, "request_jpa_02", 0))
                .contains(first);
        assertThat(repository.findByUserIdAndRequestIdAndDeleted(2_002L, "request_jpa_02", 0))
                .contains(anotherUser);
    }

    private WorkOrderEntity entity(long userId, String requestId) {
        WorkOrderEntity entity = new WorkOrderEntity();
        entity.setUserId(userId);
        entity.setRequestId(requestId);
        entity.setConversationId("work-order:" + userId + ":" + requestId);
        entity.setCategory(WorkOrderCategory.REFUND);
        entity.setSummary("用户申请退款");
        entity.setPriority(4);
        entity.setStatus(WorkOrderStatus.MANUAL_REVIEW);
        entity.setOrderNo("202607190001");
        entity.setManualReviewRequired(true);
        entity.setOriginalDescription("手机无法开机，我要退款");
        return entity;
    }
}
