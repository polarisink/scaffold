package com.scaffold.provider;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderTransactionRecordRepository extends JpaRepository<ProviderTransactionRecord, Long> {

    long countByBusinessKey(String businessKey);
}
