package com.scaffold.swagger.starter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "scaffold.swagger.enabled=true")
class SwaggerApiDocsIntegrationTest {

    private final MockMvc mockMvc;

    @Autowired
    SwaggerApiDocsIntegrationTest(WebApplicationContext context) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void shouldGenerateApiDocsWhenControllerAdviceIsPresent() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").isNotEmpty());
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import({TestController.class, TestExceptionHandler.class})
    static class TestApplication {
    }

    @RestController
    static class TestController {

        @GetMapping("/test")
        String test() {
            return "ok";
        }
    }

    @RestControllerAdvice
    static class TestExceptionHandler {

        @ExceptionHandler(Exception.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        String handle(Exception exception) {
            return exception.getMessage();
        }
    }
}
