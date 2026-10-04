package com.monefy.controller;

import com.monefy.entity.Transfer;
import com.monefy.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Date;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TransferControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransferService transferService;

    @InjectMocks
    private TransferController transferController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(transferController).build();
    }

    @Test
    void testGetAllTransfers() throws Exception {
        Transfer transfer1 = new Transfer(1L, new Date(System.currentTimeMillis()), 1, 2, 100, "Description1");
        Transfer transfer2 = new Transfer(2L, new Date(System.currentTimeMillis()), 2, 3, 200, "Description2");

        when(transferService.getAllTransfers()).thenReturn(Arrays.asList(transfer1, transfer2));

        mockMvc.perform(get("/transfers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(100.0))
                .andExpect(jsonPath("$[1].amount").value(200.0));
    }

    @Test
    void testGetTransferById() throws Exception {
        Transfer transfer = new Transfer(1L, new Date(System.currentTimeMillis()), 1, 2, 100, "Description1");

        when(transferService.getTransferById(anyLong())).thenReturn(Optional.of(transfer));

        mockMvc.perform(get("/transfers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100.0));
    }

    @Test
    void testCreateTransfer() throws Exception {
        Transfer transfer = new Transfer(1L, new Date(System.currentTimeMillis()), 1, 2, 100, "Description1");

        when(transferService.saveTransfer(any(Transfer.class))).thenReturn(transfer);

        mockMvc.perform(post("/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\":100.0,\"date\":\"2023-10-10\",\"fromAccountId\":1,\"toAccountId\":2,\"description\":\"Description1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100.0));
    }

    @Test
    void testDeleteTransfer() throws Exception {
        mockMvc.perform(delete("/transfers/1"))
                .andExpect(status().isOk());
    }
}