package com.scaffold.support.knowledge.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KnowledgeDocumentRepository extends JpaRepository<KnowledgeDocumentEntity, String> {

    Optional<KnowledgeDocumentEntity> findByDocumentId(String documentId);
}
