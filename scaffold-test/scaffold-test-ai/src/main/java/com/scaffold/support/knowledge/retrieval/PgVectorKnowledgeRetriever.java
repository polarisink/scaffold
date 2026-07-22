package com.scaffold.support.knowledge.retrieval;

import com.scaffold.support.knowledge.KnowledgeDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * 使用 PostgreSQL pgvector 执行余弦相似度检索，并将向量文档还原为领域知识片段。
 */
@Service
@Lazy
@ConditionalOnProperty(prefix = "spring.ai.model", name = "embedding", havingValue = "transformers")
@RequiredArgsConstructor
public class PgVectorKnowledgeRetriever implements KnowledgeRetriever {

    private static final double SIMILARITY_THRESHOLD = 0.55;
    private final VectorStore vectorStore;

    @Override
    /** 按最低相似度阈值和数量上限搜索相关知识片段。 */
    public List<KnowledgeDocument> search(String query, int limit) {
        return vectorStore.similaritySearch(SearchRequest.builder()
                        .query(query)
                        .topK(limit)
                        .similarityThreshold(SIMILARITY_THRESHOLD)
                        .build()).stream()
                .map(chunk -> new KnowledgeDocument(
                        String.valueOf(chunk.getMetadata().get("documentId")),
                        String.valueOf(chunk.getMetadata().get("title")),
                        String.valueOf(chunk.getMetadata().get("version")),
                        Instant.parse(String.valueOf(chunk.getMetadata().get("updatedAt"))),
                        chunk.getText()))
                .toList();
    }
}
