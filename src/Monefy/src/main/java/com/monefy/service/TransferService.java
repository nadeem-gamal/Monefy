package com.monefy.service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monefy.entity.Account;
import com.monefy.entity.Transfer;
import com.monefy.exception.BusinessRuleException;
import com.monefy.exception.ResourceNotFoundException;
import com.monefy.repository.AccountRepository;
import com.monefy.repository.TransferRepository;

/**
 * A transfer moves a positive amount from one account to another.
 * For now both accounts must use the same currency (no conversion yet).
 */
@Service
public class TransferService {

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<Transfer> getAllTransfers() {
        return transferRepository.findAll();
    }

    public Optional<Transfer> getTransferById(Long id) {
        return transferRepository.findById(id);
    }

    /**
     * Creates the transfer when it has no id, otherwise updates the existing one
     * by undoing the old movement and applying the new one.
     */
    @Transactional
    public Transfer saveTransfer(Transfer transfer) {
        validate(transfer);

        if (transfer.getId() == null) {
            moveMoney(transfer.getFromAccountId(), transfer.getToAccountId(), transfer.getAmount());
            return transferRepository.save(transfer);
        }

        Transfer existing = transferRepository.findById(transfer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transfer not found: " + transfer.getId()));
        int oldFrom = existing.getFromAccountId();
        int oldTo = existing.getToAccountId();
        float oldAmount = existing.getAmount();

        moveMoney(oldTo, oldFrom, oldAmount);
        moveMoney(transfer.getFromAccountId(), transfer.getToAccountId(), transfer.getAmount());
        return transferRepository.save(transfer);
    }

    @Transactional
    public void deleteTransfer(Long id) {
        Transfer existing = transferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer not found: " + id));

        moveMoney(existing.getToAccountId(), existing.getFromAccountId(), existing.getAmount());
        transferRepository.delete(existing);
    }

    private void validate(Transfer transfer) {
        if (transfer.getAmount() <= 0) {
            throw new BusinessRuleException("Transfer amount must be greater than zero");
        }
        if (transfer.getFromAccountId() == transfer.getToAccountId()) {
            throw new BusinessRuleException("Cannot transfer to the same account");
        }
        if (transfer.getDate() == null) {
            transfer.setDate(new Date(System.currentTimeMillis()));
        }
    }

    /**
     * Takes amount out of fromId and puts it into toId.
     * Accounts are locked in ascending id order so two opposite transfers can't deadlock.
     */
    private void moveMoney(int fromId, int toId, float amount) {
        int firstId = Math.min(fromId, toId);
        int secondId = Math.max(fromId, toId);
        Account first = lockAccount(firstId);
        Account second = lockAccount(secondId);

        Account source = (fromId == firstId) ? first : second;
        Account target = (fromId == firstId) ? second : first;

        if (source.getCurrency() != target.getCurrency()) {
            throw new BusinessRuleException(
                    "Transfers between accounts with different currencies are not supported yet");
        }

        source.setBalance(BalanceCalculator.subtract(source.getBalance(), amount));
        target.setBalance(BalanceCalculator.add(target.getBalance(), amount));
        accountRepository.save(source);
        accountRepository.save(target);
    }

    private Account lockAccount(long accountId) {
        return accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountId));
    }
}
