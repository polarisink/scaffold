package com.scaffold.support.knowledge.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgeDocumentRepository extends JpaRepository<KnowledgeDocumentEntity, String> {
}
