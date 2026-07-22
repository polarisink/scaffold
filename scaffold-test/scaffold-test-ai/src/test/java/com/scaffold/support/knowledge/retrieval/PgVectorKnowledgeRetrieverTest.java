package com.scaffold.support.knowledge.retrieval;

import com.scaffold.support.knowledge.KnowledgeDocument;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** 验证 pgvector 相似度检索和最低相关度过滤。 */
class PgVectorKnowledgeRetrieverTest {

    @Test
    void searchesPgvectorWithThresholdAndRestoresSourceMetadata() {
        VectorStore vectorStore = mock(VectorStore.class);
        Document chunk = new Document("refund-policy:0", "七日内可以申请退款", Map.of(
                "documentId", "refund-policy", "title", "退款政策", "version", "2.0",
                "updatedAt", "2026-07-21T00:00:00Z", "chunkIndex", 0));
        when(vectorStore.similaritySearch(org.mockito.ArgumentMatchers.any(SearchRequest.class)))
                .thenReturn(List.of(chunk));
        PgVectorKnowledgeRetriever retriever = new PgVectorKnowledgeRetriever(vectorStore);

        List<KnowledgeDocument> result = retriever.search("质量问题怎么退货", 3);

        assertThat(result).singleElement().satisfies(document -> {
            assertThat(document.documentId()).isEqualTo("refund-policy");
            assertThat(document.content()).isEqualTo("七日内可以申请退款");
        });
        ArgumentCaptor<SearchRequest> request = ArgumentCaptor.forClass(SearchRequest.class);
        verify(vectorStore).similaritySearch(request.capture());
        assertThat(request.getValue().getTopK()).isEqualTo(3);
        assertThat(request.getValue().getSimilarityThreshold()).isEqualTo(0.55);
    }
}
