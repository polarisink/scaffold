package com.scaffold.support.workorder;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 售后工单的 JPA 数据访问接口，所有业务查询均包含用户和逻辑删除条件。
 */
public interface WorkOrderRepository extends JpaRepository<WorkOrderEntity, Long> {

    /**
     * 按用户和请求标识查找已有工单，用于实现幂等创建。
     */
    Optional<WorkOrderEntity> findByUserIdAndRequestIdAndDeleted(long userId, String requestId, int deleted);

    /**
     * 查询当前用户可访问的指定工单。
     */
    Optional<WorkOrderEntity> findByIdAndUserIdAndDeleted(long id, long userId, int deleted);

    /**
     * 按创建时间倒序查询当前用户全部有效工单。
     */
    List<WorkOrderEntity> findByUserIdAndDeletedOrderByGmtCreatedDescIdDesc(long userId, int deleted);
}
