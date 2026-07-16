package com.scaffold.satoken.servlet;

import cn.dev33.satoken.exception.NotLoginException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SaTokenExceptionHandlerTest.TestApplication.class)
@AutoConfigureMockMvc
class SaTokenExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsUnauthorizedWhenTokenIsMissing() throws Exception {
        mockMvc.perform(get("/test/protected"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(40100))
                .andExpect(jsonPath("$.message").value("未授权"));
    }

    @RestController
    static class TestController {

        @GetMapping("/test/protected")
        void protectedEndpoint() {
            throw NotLoginException.newInstance(
                    "login",
                    NotLoginException.NOT_TOKEN,
                    NotLoginException.NOT_TOKEN_MESSAGE,
                    null);
        }
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import({SaTokenExceptionHandler.class, TestController.class})
    static class TestApplication {
    }
}
