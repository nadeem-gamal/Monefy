package com.monefy.controller;

import com.monefy.entity.Transaction;
import com.monefy.service.TransactionService;
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

class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    @Test
    void testGetAllTransactions() throws Exception {
        Transaction transaction1 = new Transaction(1L, new Date(System.currentTimeMillis()), 1, 1, 100, "Description1");
        Transaction transaction2 = new Transaction(2L, new Date(System.currentTimeMillis()), 2, 2, 200, "Description2");

        when(transactionService.getAllTransactions()).thenReturn(Arrays.asList(transaction1, transaction2));

        mockMvc.perform(get("/transaction/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(100.0))
                .andExpect(jsonPath("$[1].amount").value(200.0));
    }

    @Test
    void testGetTransactionById() throws Exception {
        Transaction transaction = new Transaction(1L, new Date(System.currentTimeMillis()), 1, 1, 100, "Description1");

        when(transactionService.getTransactionById(anyLong())).thenReturn(Optional.of(transaction));

        mockMvc.perform(get("/transaction/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100.0));
    }

    @Test
    void testCreateTransaction() throws Exception {
        Transaction transaction = new Transaction(1L, new Date(System.currentTimeMillis()), 1, 1, 100, "Description1");

        when(transactionService.saveTransaction(any(Transaction.class))).thenReturn(transaction);

        mockMvc.perform(post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\":100.0,\"date\":\"2023-10-10\",\"description\":\"Description1\",\"accountId\":1,\"categoryId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100.0));
    }

    @Test
    void testDeleteTransaction() throws Exception {
        mockMvc.perform(delete("/transaction/1"))
                .andExpect(status().isOk());
    }
}