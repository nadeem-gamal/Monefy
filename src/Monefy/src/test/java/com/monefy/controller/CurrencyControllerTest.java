package com.monefy.controller;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.monefy.entity.Currency;
import com.monefy.service.CurrencyService;

class CurrencyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private CurrencyController currencyController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(currencyController).build();
    }

    @Test
    void testGetAllCurrencies() throws Exception {
        Currency currency1 = new Currency(1L, "Currency1", "CUR1", "$");
        Currency currency2 = new Currency(2L, "Currency2", "CUR2", "€");

        when(currencyService.getAllCurrencies()).thenReturn(Arrays.asList(currency1, currency2));

        mockMvc.perform(get("/currency/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Currency1"))
                .andExpect(jsonPath("$[1].name").value("Currency2"));
    }

    @Test
    void testGetCurrencyById() throws Exception {
        Currency currency = new Currency(1L, "Currency1", "CUR1", "$");

        when(currencyService.getCurrencyById(anyLong())).thenReturn(Optional.of(currency));

        mockMvc.perform(get("/currency/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Currency1"));
    }

    @Test
    void testCreateCurrency() throws Exception {
        Currency currency = new Currency(1L, "Currency1", "CUR1", "$");

        when(currencyService.saveCurrency(any(Currency.class))).thenReturn(currency);

        mockMvc.perform(post("/currency")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Currency1\",\"code\":\"CUR1\",\"symbol\":\"$\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Currency1"));
    }

    @Test
    void testDeleteCurrency() throws Exception {
        mockMvc.perform(delete("/currency/1"))
                .andExpect(status().isOk());
    }
}