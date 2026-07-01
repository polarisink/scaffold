package com.scaffold.repo;

import com.scaffold.entity.ConsumerTransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumerTransactionRecordRepository
        extends JpaRepository<ConsumerTransactionRecord, Long> {

    long countByBusinessKey(String businessKey);
}
