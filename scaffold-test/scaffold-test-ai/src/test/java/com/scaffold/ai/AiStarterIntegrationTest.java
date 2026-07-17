package com.scaffold.ai;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AiStarterIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void servesFrontendAndChatsThroughStarter() {
        ResponseEntity<String> page = restTemplate.getForEntity("/", String.class);
        assertThat(page.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(page.getBody()).contains("Scaffold AI Lab");

        Map<?, ?> reply = restTemplate.postForObject("/api/ai/chat",
                Map.of("conversationId", "test", "message", "你好 AI"), Map.class);
        assertThat(reply.get("content")).isEqualTo("本地测试模型收到：你好 AI");
    }

    @Test
    void discoversAndInvokesAnnotatedTools() {
        ResponseEntity<String> tools = restTemplate.getForEntity("/api/ai/tools", String.class);
        assertThat(tools.getBody()).contains("currentTime", "addNumbers");

        Map<?, ?> result = restTemplate.postForObject("/api/ai/tools/addNumbers/invoke",
                Map.of("left", 19, "right", 23), Map.class);
        assertThat(result.get("result")).isEqualTo("42");
    }

    @Test
    void streamsChatAsServerSentEvents() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange("/api/ai/chat/stream", HttpMethod.POST,
                new HttpEntity<>(Map.of("conversationId", "stream-test", "message", "流式你好"), headers),
                String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.TEXT_EVENT_STREAM);
        assertThat(response.getBody()).contains("本地测试模型收到：流式你好");
    }
}
