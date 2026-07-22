package com.scaffold.support.refund;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 待确认退款操作的数据访问接口。
 */
public interface PendingActionRepository extends JpaRepository<PendingActionEntity, Long> {

    Optional<PendingActionEntity> findByConfirmationIdAndDeleted(String confirmationId, int deleted);

    /**
     * 锁定确认记录，防止并发请求重复执行退款。
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select action from PendingActionEntity action where action.confirmationId = :confirmationId "
            + "and action.deleted = :deleted")
    Optional<PendingActionEntity> findForUpdate(@Param("confirmationId") String confirmationId,
                                                @Param("deleted") int deleted);
}
