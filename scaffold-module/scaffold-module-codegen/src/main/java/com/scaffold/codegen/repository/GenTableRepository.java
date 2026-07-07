package com.scaffold.codegen.repository;

import com.scaffold.codegen.entity.GenTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GenTableRepository extends JpaRepository<GenTableEntity, Long> {

    List<GenTableEntity> findByDeletedOrderByGmtModifiedDescIdDesc(Integer deleted);

    Optional<GenTableEntity> findByIdAndDeleted(Long id, Integer deleted);

    Optional<GenTableEntity> findFirstByTableNameOrderByDeletedAscIdDesc(String tableName);
}
