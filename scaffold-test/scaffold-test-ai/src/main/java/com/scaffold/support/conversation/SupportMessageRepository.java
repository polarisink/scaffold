package com.scaffold.support.conversation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SupportMessageRepository extends JpaRepository<SupportMessageEntity, Long> {

    List<SupportMessageEntity> findTop20ByWorkOrderIdOrderBySequenceDesc(Long workOrderId);

    @Query("select coalesce(max(m.sequence), 0) from SupportMessageEntity m where m.workOrderId = :workOrderId")
    long findMaxSequence(@Param("workOrderId") Long workOrderId);

    @Modifying
    void deleteByWorkOrderId(Long workOrderId);
}
