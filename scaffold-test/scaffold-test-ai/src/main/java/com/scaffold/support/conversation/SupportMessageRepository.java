package com.scaffold.support.conversation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SupportMessageRepository extends JpaRepository<SupportMessageEntity, Long> {

    /**
     * 查询指定工单最近保存的 20 条消息。
     *
     * @param workOrderId 工单 ID
     * @return 按消息序号倒序排列的消息列表；没有消息时返回空列表
     */
    List<SupportMessageEntity> findTop20ByWorkOrderIdOrderBySequenceDesc(Long workOrderId);

    /**
     * 查询指定工单当前最大的消息序号，用于计算下一条消息的递增序号。
     *
     * @param workOrderId 工单 ID
     * @return 当前最大消息序号；工单尚无消息时返回 {@code 0}
     */
    @Query("select coalesce(max(m.sequence), 0) from SupportMessageEntity m where m.workOrderId = :workOrderId")
    long findMaxSequence(@Param("workOrderId") Long workOrderId);

    /**
     * 删除指定工单下的全部持久化消息。
     *
     * @param workOrderId 工单 ID
     */
    @Modifying
    void deleteByWorkOrderId(Long workOrderId);
}
