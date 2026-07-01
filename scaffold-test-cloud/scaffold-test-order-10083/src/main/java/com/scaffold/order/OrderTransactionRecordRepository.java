package com.scaffold.order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderTransactionRecordRepository extends JpaRepository<OrderTransactionRecord, Long> {

    long countByBusinessKey(String businessKey);
}
