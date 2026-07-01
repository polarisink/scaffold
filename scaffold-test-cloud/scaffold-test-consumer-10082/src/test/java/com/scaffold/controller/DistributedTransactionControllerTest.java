package com.scaffold.controller;

import com.scaffold.service.DistributedTransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DistributedTransactionControllerTest {

    @Test
    void shouldResolveUnnamedPathVariablesFromCompilerMetadata() throws Exception {
        DistributedTransactionService transactionService = mock(DistributedTransactionService.class);
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new DistributedTransactionController(transactionService))
                .build();

        mockMvc.perform(post("/api/seata/transactions/tx-001/false"))
                .andExpect(status().isOk());

        verify(transactionService).execute("tx-001", false);
    }
}
