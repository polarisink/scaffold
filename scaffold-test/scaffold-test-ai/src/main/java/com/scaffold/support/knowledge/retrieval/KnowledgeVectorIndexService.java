package com.scaffold.support.knowledge.retrieval;

import com.scaffold.support.knowledge.persistence.KnowledgeDocumentEntity;
import com.scaffold.support.knowledge.persistence.KnowledgeDocumentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 将 PostgreSQL 中的知识文档切片、向量化并重建 pgvector 索引。
 */
@Service
@Lazy
@ConditionalOnProperty(prefix = "spring.ai.model", name = "embedding", havingValue = "transformers")
@RequiredArgsConstructor
public class KnowledgeVectorIndexService {

    private static final int MAX_CHUNK_CHARACTERS = 800;
    private final KnowledgeDocumentRepository repository;
    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;

    /** 全量重建知识向量表，保证删除和更新后的索引与文档事实一致。 */
    @Transactional
    public void rebuild() {
        jdbcTemplate.execute("TRUNCATE TABLE ai_knowledge_vector");
        List<Document> chunks = repository.findAll().stream().flatMap(entity -> chunks(entity).stream()).toList();
        if (!chunks.isEmpty()) {
            vectorStore.add(chunks);
        }
    }

    private List<Document> chunks(KnowledgeDocumentEntity entity) {
        List<String> texts = split(entity.getContent());
        List<Document> documents = new ArrayList<>(texts.size());
        for (int index = 0; index < texts.size(); index++) {
            Map<String, Object> metadata = Map.of(
                    "documentId", entity.getDocumentId(),
                    "title", entity.getTitle(),
                    "version", entity.getVersion(),
                    "updatedAt", entity.getUpdatedAt().toString(),
                    "chunkIndex", index);
            documents.add(new Document(entity.getDocumentId() + ":" + index, texts.get(index), metadata));
        }
        return documents;
    }

    private List<String> split(String content) {
        List<String> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String paragraph : content.split("\\R\\s*\\R")) {
            String normalized = paragraph.trim();
            if (normalized.isEmpty()) continue;
            if (!current.isEmpty() && current.length() + normalized.length() + 2 > MAX_CHUNK_CHARACTERS) {
                chunks.add(current.toString());
                current.setLength(0);
            }
            if (normalized.length() > MAX_CHUNK_CHARACTERS) {
                if (!current.isEmpty()) {
                    chunks.add(current.toString());
                    current.setLength(0);
                }
                for (int start = 0; start < normalized.length(); start += MAX_CHUNK_CHARACTERS) {
                    chunks.add(normalized.substring(start, Math.min(normalized.length(), start + MAX_CHUNK_CHARACTERS)));
                }
            } else {
                if (!current.isEmpty()) current.append("\n\n");
                current.append(normalized);
            }
        }
        if (!current.isEmpty()) chunks.add(current.toString());
        return chunks;
    }
}
