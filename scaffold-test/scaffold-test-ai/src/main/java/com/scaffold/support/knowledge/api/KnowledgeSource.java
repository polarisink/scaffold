package com.scaffold.support.knowledge.api;

import java.time.Instant;

/**
 * 知识库回答引用的文档元数据。
 */
public record KnowledgeSource(String documentId, String title, String version, Instant updatedAt) {
}
