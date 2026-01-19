package com.monefy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.monefy.entity.Currency;
import com.monefy.repository.CurrencyRepository;

@Service
public class CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    public Optional<Currency> getCurrencyById(Long id) {
        return currencyRepository.findById(id);
    }

    public Currency saveCurrency(Currency currency) {
        return currencyRepository.save(currency);
    }

    public void deleteCurrency(Long id) {
        currencyRepository.deleteById(id);
    }
}