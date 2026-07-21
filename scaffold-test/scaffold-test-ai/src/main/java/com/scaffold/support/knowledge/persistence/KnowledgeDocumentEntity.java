package com.scaffold.support.knowledge.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "ai_knowledge_document")
public class KnowledgeDocumentEntity {
    @Id
    @Column(name = "document_id", length = 100)
    private String documentId;
    @Column(nullable = false, length = 200)
    private String title;
    @Column(nullable = false, length = 32)
    private String version;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @Column(nullable = false, columnDefinition = "text")
    private String content;
}
