package com.monefy.controller;

import com.monefy.entity.Account;
import com.monefy.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test
    void testGetAllAccounts() throws Exception {
        Account account1 = new Account(1L, "Account1", 1, null, 0, null, false, 0, 0);
        Account account2 = new Account(2L, "Account2", 2, null, 0, null, false, 0, 0);

        when(accountService.getAllAccounts()).thenReturn(Arrays.asList(account1, account2));

        mockMvc.perform(get("/account/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Account1"))
                .andExpect(jsonPath("$[1].name").value("Account2"));
    }

    @Test
    void testGetAccountById() throws Exception {
        Account account = new Account(1L, "Account1", 1, null, 0, null, false, 0, 0);

        when(accountService.getAccountById(anyLong())).thenReturn(Optional.of(account));

        mockMvc.perform(get("/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Account1"));
    }

    @Test
    void testCreateAccount() throws Exception {
        Account account = new Account(1L, "Account1", 1, null, 0, null, false, 0, 0);

        when(accountService.saveAccount(any(Account.class))).thenReturn(account);

        mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Account1\",\"currency\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Account1"));
    }

    @Test
    void testDeleteAccount() throws Exception {
        mockMvc.perform(delete("/account/1"))
                .andExpect(status().isOk());
    }
}