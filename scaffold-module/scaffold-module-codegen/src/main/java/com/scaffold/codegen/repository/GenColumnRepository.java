package com.scaffold.codegen.repository;

import com.scaffold.codegen.entity.GenColumnEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface GenColumnRepository extends JpaRepository<GenColumnEntity, Long> {

    List<GenColumnEntity> findByTableIdAndDeletedOrderBySortNoAscIdAsc(Long tableId, Integer deleted);

    List<GenColumnEntity> findByTableIdIn(Collection<Long> tableIds);
}
