package com.monefy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.monefy.entity.Transfer;
import com.monefy.exception.BusinessRuleException;
import com.monefy.exception.ResourceNotFoundException;
import com.monefy.repository.AccountRepository;
import com.monefy.repository.TransferRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransferServiceTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransferService transferService;

    private Account account(long id, int currency, float balance) {
        Account account = new Account();
        account.setId(id);
        account.setCurrency(currency);
        account.setBalance(balance);
        when(accountRepository.findByIdForUpdate(id)).thenReturn(Optional.of(account));
        return account;
    }

    private Transfer transfer(Long id, int from, int to, float amount) {
        Transfer transfer = new Transfer();
        transfer.setId(id);
        transfer.setFromAccountId(from);
        transfer.setToAccountId(to);
        transfer.setAmount(amount);
        return transfer;
    }

    @Test
    void transferMovesMoneyBetweenAccounts() {
        Account from = account(1L, 1, 100f);
        Account to = account(2L, 1, 20f);
        when(transferRepository.save(any(Transfer.class))).thenAnswer(i -> i.getArgument(0));

        transferService.saveTransfer(transfer(null, 1, 2, 30f));

        assertEquals(70f, from.getBalance(), 0.001f);
        assertEquals(50f, to.getBalance(), 0.001f);
    }

    @Test
    void transferFromHigherIdToLowerIdAlsoWorks() {
        Account low = account(1L, 1, 20f);
        Account high = account(2L, 1, 100f);
        when(transferRepository.save(any(Transfer.class))).thenAnswer(i -> i.getArgument(0));

        transferService.saveTransfer(transfer(null, 2, 1, 30f));

        assertEquals(50f, low.getBalance(), 0.001f);
        assertEquals(70f, high.getBalance(), 0.001f);
    }

    @Test
    void updateUndoesOldTransferAndAppliesNewOne() {
        // existing transfer: 30 from account 1 to 2 (already in the balances below)
        Account first = account(1L, 1, 70f);
        Account second = account(2L, 1, 50f);
        when(transferRepository.findById(8L)).thenReturn(Optional.of(transfer(8L, 1, 2, 30f)));
        when(transferRepository.save(any(Transfer.class))).thenAnswer(i -> i.getArgument(0));

        // edited: 50 instead of 30
        transferService.saveTransfer(transfer(8L, 1, 2, 50f));

        assertEquals(50f, first.getBalance(), 0.001f);
        assertEquals(70f, second.getBalance(), 0.001f);
    }

    @Test
    void deleteUndoesTheTransfer() {
        Account first = account(1L, 1, 70f);
        Account second = account(2L, 1, 50f);
        Transfer existing = transfer(8L, 1, 2, 30f);
        when(transferRepository.findById(8L)).thenReturn(Optional.of(existing));

        transferService.deleteTransfer(8L);

        assertEquals(100f, first.getBalance(), 0.001f);
        assertEquals(20f, second.getBalance(), 0.001f);
        verify(transferRepository).delete(existing);
    }

    @Test
    void sameAccountIsRejected() {
        assertThrows(BusinessRuleException.class,
                () -> transferService.saveTransfer(transfer(null, 1, 1, 30f)));
    }

    @Test
    void nonPositiveAmountIsRejected() {
        assertThrows(BusinessRuleException.class,
                () -> transferService.saveTransfer(transfer(null, 1, 2, 0f)));
        assertThrows(BusinessRuleException.class,
                () -> transferService.saveTransfer(transfer(null, 1, 2, -5f)));
    }

    @Test
    void differentCurrenciesAreRejected() {
        account(1L, 1, 100f);
        account(2L, 2, 20f);

        assertThrows(BusinessRuleException.class,
                () -> transferService.saveTransfer(transfer(null, 1, 2, 30f)));

        verify(transferRepository, never()).save(any(Transfer.class));
    }

    @Test
    void unknownAccountIsRejected() {
        account(1L, 1, 100f);
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transferService.saveTransfer(transfer(null, 1, 2, 30f)));

        verify(transferRepository, never()).save(any(Transfer.class));
    }
}
