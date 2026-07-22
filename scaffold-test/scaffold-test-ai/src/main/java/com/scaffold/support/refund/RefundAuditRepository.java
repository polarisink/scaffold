package com.scaffold.support.refund;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 退款业务审计记录的数据访问接口。
 */
public interface RefundAuditRepository extends JpaRepository<RefundAuditEntity, Long> {
}
