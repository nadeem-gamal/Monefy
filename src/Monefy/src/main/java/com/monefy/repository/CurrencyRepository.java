package com.monefy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monefy.entity.Currency;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

}
