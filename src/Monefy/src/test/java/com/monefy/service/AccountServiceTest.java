package com.monefy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.monefy.entity.Account;
import com.monefy.exception.BusinessRuleException;
import com.monefy.exception.ResourceNotFoundException;
import com.monefy.repository.AccountRepository;
import com.monefy.repository.TransactionRepository;
import com.monefy.repository.TransferRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransferRepository transferRepository;

    @InjectMocks
    private AccountService accountService;

    private Account newAccount(String name, int currency, int userId, float initialBalance) {
        Account account = new Account();
        account.setName(name);
        account.setCurrency(currency);
        account.setUserId(userId);
        account.setInitialAccountBalance(initialBalance);
        return account;
    }

    @Test
    void createStartsBalanceAtInitialBalanceAndKeepsName() {
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));
        Account account = newAccount("  Cash  ", 1, 1, 250f);
        account.setBalance(999f); // a client-supplied balance must be ignored

        Account saved = accountService.saveAccount(account);

        assertEquals("Cash", saved.getName());
        assertEquals(250f, saved.getBalance(), 0.001f);
        assertNotNull(saved.getInitialBalanceDate());
    }

    @Test
    void missingNameIsRejected() {
        assertThrows(BusinessRuleException.class,
                () -> accountService.saveAccount(newAccount(null, 1, 1, 0f)));
        assertThrows(BusinessRuleException.class,
                () -> accountService.saveAccount(newAccount("   ", 1, 1, 0f)));

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void missingCurrencyOrUserIsRejected() {
        assertThrows(BusinessRuleException.class,
                () -> accountService.saveAccount(newAccount("Cash", 0, 1, 0f)));
        assertThrows(BusinessRuleException.class,
                () -> accountService.saveAccount(newAccount("Cash", 1, 0, 0f)));
    }

    @Test
    void updateKeepsBalanceAndShiftsItWhenInitialBalanceChanges() {
        Account existing = newAccount("Cash", 1, 1, 100f);
        existing.setId(3L);
        existing.setBalance(160f); // 100 initial + 60 from transactions
        when(accountRepository.findByIdForUpdate(3L)).thenReturn(Optional.of(existing));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        Account edited = newAccount("Cash wallet", 1, 1, 120f);
        edited.setId(3L);
        edited.setBalance(0f); // ignored

        Account saved = accountService.saveAccount(edited);

        assertEquals("Cash wallet", saved.getName());
        assertEquals(180f, saved.getBalance(), 0.001f);
    }

    @Test
    void updatingUnknownAccountIsRejected() {
        when(accountRepository.findByIdForUpdate(9L)).thenReturn(Optional.empty());
        Account edited = newAccount("Cash", 1, 1, 0f);
        edited.setId(9L);

        assertThrows(ResourceNotFoundException.class, () -> accountService.saveAccount(edited));
    }

    @Test
    void accountWithTransactionsCannotBeDeleted() {
        when(accountRepository.existsById(3L)).thenReturn(true);
        when(transactionRepository.existsByAccountId(3)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> accountService.deleteAccount(3L));

        verify(accountRepository, never()).deleteById(3L);
    }

    @Test
    void currencyCannotChangeOnceAccountHasTransactions() {
        Account existing = newAccount("Cash", 1, 1, 100f);
        existing.setId(3L);
        when(accountRepository.findByIdForUpdate(3L)).thenReturn(Optional.of(existing));
        when(transactionRepository.existsByAccountId(3)).thenReturn(true);

        Account edited = newAccount("Cash", 2, 1, 100f);
        edited.setId(3L);

        assertThrows(BusinessRuleException.class, () -> accountService.saveAccount(edited));

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void currencyCanChangeWhileAccountIsUnused() {
        Account existing = newAccount("Cash", 1, 1, 100f);
        existing.setId(3L);
        when(accountRepository.findByIdForUpdate(3L)).thenReturn(Optional.of(existing));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        Account edited = newAccount("Cash", 2, 1, 100f);
        edited.setId(3L);

        Account saved = accountService.saveAccount(edited);

        assertEquals(2, saved.getCurrency());
    }

    @Test
    void unusedAccountCanBeDeleted() {
        when(accountRepository.existsById(3L)).thenReturn(true);

        accountService.deleteAccount(3L);

        verify(accountRepository).deleteById(3L);
    }
}
