package com.monefy.service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monefy.entity.Account;
import com.monefy.entity.Transaction;
import com.monefy.exception.BusinessRuleException;
import com.monefy.exception.ResourceNotFoundException;
import com.monefy.repository.AccountRepository;
import com.monefy.repository.CategoryRepository;
import com.monefy.repository.TransactionRepository;

/**
 * Amounts are signed: positive = income, negative = expense (same as the Monefy export).
 * Every change to a transaction is applied to its account's balance in the same DB transaction.
 */
@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    /**
     * Creates the transaction when it has no id, otherwise updates the existing one.
     * An update first takes the old amount back out of the old account,
     * then applies the new amount to the (possibly different) new account.
     */
    @Transactional
    public Transaction saveTransaction(Transaction transaction) {
        validate(transaction);

        if (transaction.getId() == null) {
            applyToAccount(transaction.getAccountId(), transaction.getAmount());
            return transactionRepository.save(transaction);
        }

        Transaction existing = transactionRepository.findById(transaction.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transaction.getId()));
        int oldAccountId = existing.getAccountId();
        float oldAmount = existing.getAmount();

        applyToAccount(oldAccountId, -oldAmount);
        applyToAccount(transaction.getAccountId(), transaction.getAmount());
        return transactionRepository.save(transaction);
    }

    @Transactional
    public void deleteTransaction(Long id) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + id));

        applyToAccount(existing.getAccountId(), -existing.getAmount());
        transactionRepository.delete(existing);
    }

    private void validate(Transaction transaction) {
        if (transaction.getAmount() == 0) {
            throw new BusinessRuleException("Transaction amount must not be zero");
        }
        if (!categoryRepository.existsById((long) transaction.getCategoryId())) {
            throw new ResourceNotFoundException("Category not found: " + transaction.getCategoryId());
        }
        if (transaction.getDate() == null) {
            transaction.setDate(new Date(System.currentTimeMillis()));
        }
    }

    private void applyToAccount(long accountId, float delta) {
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountId));
        account.setBalance(BalanceCalculator.add(account.getBalance(), delta));
        accountRepository.save(account);
    }
}
