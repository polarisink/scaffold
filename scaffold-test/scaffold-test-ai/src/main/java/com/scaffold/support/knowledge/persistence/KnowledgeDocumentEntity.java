package com.scaffold.support.knowledge.persistence;

import com.scaffold.orm.BaseStringAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * 售后知识文档持久化实体，向量片段保存在独立 pgvector 表中。
 */
@Getter
@Setter
@Entity
@Table(name = "ai_knowledge_document")
public class KnowledgeDocumentEntity extends BaseStringAuditable {

    /**
     * 稳定的业务文档标识。
     */
    @Column(nullable = false, unique = true, length = 100)
    private String documentId;
    /**
     * 知识文档标题。
     */
    @Column(nullable = false, length = 200)
    private String title;
    /**
     * 文档内容版本。
     */
    @Column(nullable = false, length = 32)
    private String version;
    /**
     * 知识内容的业务更新时间。
     */
    @Column(nullable = false)
    private Instant updatedAt;
    /**
     * 用于切分和向量化的完整文档正文。
     */
    @Column(nullable = false, columnDefinition = "text")
    private String content;
}
