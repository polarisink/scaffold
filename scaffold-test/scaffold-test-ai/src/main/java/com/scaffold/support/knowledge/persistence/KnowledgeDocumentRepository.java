package com.scaffold.support.knowledge.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 售后知识文档的 JPA 数据访问接口。
 */
public interface KnowledgeDocumentRepository extends JpaRepository<KnowledgeDocumentEntity, String> {

    /**
     * 按稳定的业务文档标识查询知识文档。
     */
    Optional<KnowledgeDocumentEntity> findByDocumentId(String documentId);
}
