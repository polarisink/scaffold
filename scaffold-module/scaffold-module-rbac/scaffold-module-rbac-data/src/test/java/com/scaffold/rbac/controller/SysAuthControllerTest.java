package com.scaffold.rbac.controller;

import com.scaffold.rbac.service.ISysAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SysAuthControllerTest {

    @Test
    void authenticatedUserCanLogoutThroughPostEndpoint() throws Exception {
        ISysAuthService authService = mock(ISysAuthService.class);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new SysAuthController(authService)).build();

        mvc.perform(post("/auth/logout"))
                .andExpect(status().isOk());

        verify(authService).logout();
    }
}
