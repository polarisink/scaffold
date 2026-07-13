package com.scaffold.web.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        classes = GlobalExceptionHandlerTest.TestApplication.class,
        properties = {
                "scaffold.web.response.server-error-message=自定义错误消息",
                "scaffold.web.response.raw-body-path-patterns[0]=/test/raw/**"
        }
)
@AutoConfigureMockMvc
@ImportAutoConfiguration({JacksonAutoConfiguration.class, ValidationAutoConfiguration.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void wrapsControllerResponse() throws Exception {
        mockMvc.perform(get("/test/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.message").value("pong"));
    }

    @Test
    void returnsRawBodyForConfiguredPath() throws Exception {
        mockMvc.perform(get("/test/raw/ping"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"pong\"}"));
    }

    @Test
    void usesConfiguredServerErrorMessage() throws Exception {
        mockMvc.perform(get("/test/error"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(600))
                .andExpect(jsonPath("$.message").value("自定义错误消息"));
    }

    @RestController
    static class TestController {
        @GetMapping("/test/ping")
        TestPayload ping() {
            return new TestPayload("pong");
        }

        @GetMapping("/test/raw/ping")
        TestPayload rawPing() {
            return new TestPayload("pong");
        }

        @GetMapping("/test/error")
        TestPayload error() {
            throw new IllegalArgumentException("hidden");
        }
    }

    record TestPayload(String message) {
    }

    @SpringBootApplication
    @EnableConfigurationProperties(WebProperties.class)
    @Import({GlobalExceptionHandler.class, WebConfig.class, TestController.class})
    static class TestApplication {
    }
}
