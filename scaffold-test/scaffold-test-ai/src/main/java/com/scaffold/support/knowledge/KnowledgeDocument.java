package com.scaffold.support.knowledge;

import java.time.Instant;

/**
 * 参与 RAG 检索和引用展示的知识文档片段。
 */
public record KnowledgeDocument(String documentId, String title, String version,
                                Instant updatedAt, String content) {
}
