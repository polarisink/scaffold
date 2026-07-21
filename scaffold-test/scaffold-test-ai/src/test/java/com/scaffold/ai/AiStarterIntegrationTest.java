package com.scaffold.ai;

import com.scaffold.support.knowledge.retrieval.KnowledgeRetriever;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AiStarterIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private KnowledgeRetriever knowledgeRetriever;

    @Test
    void rejectsAnonymousAccessToApplicationEndpoints() {
        ResponseEntity<String> page = restTemplate.getForEntity("/", String.class);
        assertThat(page.getStatusCode().value()).isEqualTo(401);
    }

    @Test
    void protectsToolDiscoveryWithJwtAuthentication() {
        ResponseEntity<String> tools = restTemplate.getForEntity("/api/ai/tools", String.class);
        assertThat(tools.getStatusCode().value()).isEqualTo(401);
        assertThat(tools.getBody()).contains("未授权");
    }

    @Test
    void protectsStreamingChatWithJwtAuthentication() {
        ResponseEntity<String> response = restTemplate.postForEntity("/api/ai/chat/stream", "{}", String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }
}
