package com.scaffold.support.suggestion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 综合处理建议的数据访问接口。
 */
public interface HandlingSuggestionRepository extends JpaRepository<HandlingSuggestionEntity, Long> {

    Optional<HandlingSuggestionEntity> findTopByWorkOrderIdOrderByGmtCreatedDescIdDesc(Long workOrderId);
}
