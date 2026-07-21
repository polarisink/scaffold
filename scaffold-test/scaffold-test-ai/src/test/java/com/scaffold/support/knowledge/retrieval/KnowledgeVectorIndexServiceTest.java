package com.scaffold.support.knowledge.retrieval;

import com.scaffold.support.knowledge.persistence.KnowledgeDocumentEntity;
import com.scaffold.support.knowledge.persistence.KnowledgeDocumentRepository;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KnowledgeVectorIndexServiceTest {

    @Test
    void rebuildRemovesStaleVectorsAndIndexesPersistentDocuments() {
        KnowledgeDocumentRepository repository = mock(KnowledgeDocumentRepository.class);
        VectorStore vectorStore = mock(VectorStore.class);
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        KnowledgeDocumentEntity entity = new KnowledgeDocumentEntity();
        entity.setDocumentId("refund-policy");
        entity.setTitle("退款政策");
        entity.setVersion("2.0");
        entity.setUpdatedAt(Instant.parse("2026-07-21T00:00:00Z"));
        entity.setContent("第一段。\n\n第二段。");
        when(repository.findAll()).thenReturn(List.of(entity));
        KnowledgeVectorIndexService service = new KnowledgeVectorIndexService(repository, vectorStore, jdbc);

        service.rebuild();

        verify(jdbc).execute("TRUNCATE TABLE ai_knowledge_vector");
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Document>> documents = ArgumentCaptor.forClass(List.class);
        verify(vectorStore).add(documents.capture());
        assertThat(documents.getValue()).extracting(Document::getId).containsExactly("refund-policy:0");
        assertThat(documents.getValue().getFirst().getMetadata()).containsEntry("version", "2.0");
    }
}
