package com.scaffold.support.workorder;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkOrderRepository extends JpaRepository<WorkOrderEntity, Long> {

    Optional<WorkOrderEntity> findByUserIdAndRequestIdAndDeleted(long userId, String requestId, int deleted);

    Optional<WorkOrderEntity> findByIdAndUserIdAndDeleted(long id, long userId, int deleted);

    List<WorkOrderEntity> findByUserIdAndDeletedOrderByGmtCreatedDescIdDesc(long userId, int deleted);
}
