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
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.springframework.http.HttpStatus.NOT_FOUND;

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

    @Test
    void returnsResourceBodyWithoutWrapping() throws Exception {
        mockMvc.perform(get("/test/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report.txt\""))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes("file-content".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void preservesResponseStatusExceptionStatus() throws Exception {
        mockMvc.perform(get("/test/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("文件不存在"));
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

        @GetMapping("/test/download")
        ResponseEntity<InputStreamResource> download() {
            InputStreamResource resource = new InputStreamResource(
                    new ByteArrayInputStream("file-content".getBytes(StandardCharsets.UTF_8)));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment().filename("report.txt").build().toString())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        }

        @GetMapping("/test/missing")
        TestPayload missing() {
            throw new ResponseStatusException(NOT_FOUND, "文件不存在");
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
