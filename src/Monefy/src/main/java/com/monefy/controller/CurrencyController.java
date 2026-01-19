package com.monefy.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monefy.entity.Currency;
import com.monefy.service.CurrencyService;

@RestController
@RequestMapping("/currency")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @GetMapping("/getAll")
    public List<Currency> getAllCurrencies() {
        return currencyService.getAllCurrencies();
    }

    @GetMapping("/{id}")
    public Optional<Currency> getCurrencyById(@PathVariable Long id) {
        return currencyService.getCurrencyById(id);
    }

    @PostMapping
    public Currency createCurrency(@RequestBody Currency currency) {
        return currencyService.saveCurrency(currency);
    }

    @PutMapping
    public Currency updateCurrency(@RequestBody Currency currency) {
        return currencyService.saveCurrency(currency);
    }

    @DeleteMapping("/{id}")
    public void deleteCurrency(@PathVariable Long id) {
        currencyService.deleteCurrency(id);
    }
}