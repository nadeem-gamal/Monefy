package com.monefy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.monefy.entity.Account;
import com.monefy.entity.Transaction;
import com.monefy.exception.BusinessRuleException;
import com.monefy.exception.ResourceNotFoundException;
import com.monefy.repository.AccountRepository;
import com.monefy.repository.CategoryRepository;
import com.monefy.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account account(long id, float balance) {
        Account account = new Account();
        account.setId(id);
        account.setCurrency(1);
        account.setBalance(balance);
        when(accountRepository.findByIdForUpdate(id)).thenReturn(Optional.of(account));
        return account;
    }

    private Transaction transaction(Long id, int accountId, int categoryId, float amount) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setAccountId(accountId);
        transaction.setCategoryId(categoryId);
        transaction.setAmount(amount);
        return transaction;
    }

    private void categoryExists(long id) {
        when(categoryRepository.existsById(id)).thenReturn(true);
    }

    @Test
    void incomeIncreasesBalance() {
        Account account = account(1L, 100f);
        categoryExists(2L);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        transactionService.saveTransaction(transaction(null, 1, 2, 50f));

        assertEquals(150f, account.getBalance(), 0.001f);
    }

    @Test
    void expenseDecreasesBalance() {
        Account account = account(1L, 100f);
        categoryExists(2L);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        transactionService.saveTransaction(transaction(null, 1, 2, -30f));

        assertEquals(70f, account.getBalance(), 0.001f);
    }

    @Test
    void missingDateDefaultsToToday() {
        account(1L, 0f);
        categoryExists(2L);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction saved = transactionService.saveTransaction(transaction(null, 1, 2, 10f));

        assertNotNull(saved.getDate());
    }

    @Test
    void updateMovesAmountToNewAccount() {
        // old transaction: +40 on account 1 (already included in its balance of 140)
        Account first = account(1L, 140f);
        Account second = account(2L, 10f);
        categoryExists(2L);
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(transaction(5L, 1, 2, 40f)));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        // edited: now +25 on account 2
        transactionService.saveTransaction(transaction(5L, 2, 2, 25f));

        assertEquals(100f, first.getBalance(), 0.001f);
        assertEquals(35f, second.getBalance(), 0.001f);
    }

    @Test
    void updateOnSameAccountAppliesOnlyTheDifference() {
        Account account = account(1L, 140f);
        categoryExists(2L);
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(transaction(5L, 1, 2, 40f)));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        transactionService.saveTransaction(transaction(5L, 1, 2, 60f));

        assertEquals(160f, account.getBalance(), 0.001f);
    }

    @Test
    void deleteReversesTheBalance() {
        Account account = account(1L, 140f);
        Transaction existing = transaction(5L, 1, 2, 40f);
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(existing));

        transactionService.deleteTransaction(5L);

        assertEquals(100f, account.getBalance(), 0.001f);
        verify(transactionRepository).delete(existing);
    }

    @Test
    void unknownCategoryIsRejected() {
        Account account = account(1L, 100f);
        when(categoryRepository.existsById(9L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.saveTransaction(transaction(null, 1, 9, 50f)));

        assertEquals(100f, account.getBalance(), 0.001f);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void unknownAccountIsRejected() {
        categoryExists(2L);
        when(accountRepository.findByIdForUpdate(7L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.saveTransaction(transaction(null, 7, 2, 50f)));

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void zeroAmountIsRejected() {
        account(1L, 100f);
        categoryExists(2L);

        assertThrows(BusinessRuleException.class,
                () -> transactionService.saveTransaction(transaction(null, 1, 2, 0f)));
    }

    @Test
    void deletingUnknownTransactionIsRejected() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.deleteTransaction(99L));
    }

    @Test
    void filteringPassesAccountAndDateRangeToTheRepository() {
        when(transactionRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of());

        transactionService.getTransactions(3, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));

        verify(transactionRepository).findAll(any(Specification.class), any(Sort.class));
    }

    @Test
    void filteringWithNoArgumentsStillCallsTheRepository() {
        when(transactionRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of());

        transactionService.getTransactions(null, null, null);

        verify(transactionRepository).findAll(any(Specification.class), any(Sort.class));
    }

    @Test
    void fromAfterToIsRejected() {
        assertThrows(BusinessRuleException.class,
                () -> transactionService.getTransactions(null, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1)));
    }
}
