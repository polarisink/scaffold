package com.scaffold.sse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApiController.class)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SseConnectionManager connectionManager;

    @Test
    void disconnectsOwnedConnection() throws Exception {
        when(connectionManager.disconnect("user-1", "connection-1")).thenReturn(true);

        mockMvc.perform(delete("/api/sse/connections/{connectionId}", "connection-1")
                        .param("userId", "user-1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"disconnected\":true}"));

        verify(connectionManager).disconnect("user-1", "connection-1");
    }

    @Test
    void reportsWhenConnectionCannotBeDisconnected() throws Exception {
        when(connectionManager.disconnect("user-2", "connection-1")).thenReturn(false);

        mockMvc.perform(delete("/api/sse/connections/{connectionId}", "connection-1")
                        .param("userId", "user-2"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"disconnected\":false}"));
    }
}
