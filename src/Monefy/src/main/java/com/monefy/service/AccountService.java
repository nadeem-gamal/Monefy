package com.monefy.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monefy.entity.Account;
import com.monefy.exception.BusinessRuleException;
import com.monefy.exception.ResourceNotFoundException;
import com.monefy.repository.AccountRepository;
import com.monefy.repository.TransactionRepository;
import com.monefy.repository.TransferRepository;

/**
 * The balance is owned by the system: it starts at the initial balance and is then
 * changed only by transactions and transfers. Clients can't set it directly.
 */
@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransferRepository transferRepository;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    @Transactional
    public Account createAccount(Account account) {
        validate(account);
        account.setId(null);
        account.setInitialBalanceDate(new Date());
        account.setBalance(account.getInitialAccountBalance());
        return accountRepository.save(account);
    }

    /**
     * Creates the account when it has no id, otherwise updates it.
     * On update the current balance is kept; if the initial balance was edited,
     * the balance moves by the same difference.
     */
    @Transactional
    public Account saveAccount(Account account) {
        if (account.getId() == null) {
            return createAccount(account);
        }

        validate(account);

        Account existing = accountRepository.findByIdForUpdate(account.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + account.getId()));

        if (account.getCurrency() != existing.getCurrency() && hasActivity(existing.getId())) {
            throw new BusinessRuleException(
                    "Currency can't be changed once the account has transactions or transfers");
        }

        float initialBalanceChange = account.getInitialAccountBalance() - existing.getInitialAccountBalance();
        account.setBalance(BalanceCalculator.add(existing.getBalance(), initialBalanceChange));
        if (account.getInitialBalanceDate() == null) {
            account.setInitialBalanceDate(existing.getInitialBalanceDate());
        }
        return accountRepository.save(account);
    }

    private boolean hasActivity(Long id) {
        int accountId = id.intValue();
        return transactionRepository.existsByAccountId(accountId)
                || transferRepository.existsByFromAccountIdOrToAccountId(accountId, accountId);
    }

    private void validate(Account account) {
        if (account.getName() == null || account.getName().isBlank()) {
            throw new BusinessRuleException("Account name is required");
        }
        if (account.getCurrency() <= 0) {
            throw new BusinessRuleException("Account currency is required");
        }
        if (account.getUserId() <= 0) {
            throw new BusinessRuleException("Account user is required");
        }
        account.setName(account.getName().trim());
    }

    @Transactional
    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new ResourceNotFoundException("Account not found: " + id);
        }
        int accountId = id.intValue();
        if (transactionRepository.existsByAccountId(accountId)
                || transferRepository.existsByFromAccountIdOrToAccountId(accountId, accountId)) {
            throw new BusinessRuleException(
                    "Account still has transactions or transfers; delete or move them first");
        }
        accountRepository.deleteById(id);
    }
}
