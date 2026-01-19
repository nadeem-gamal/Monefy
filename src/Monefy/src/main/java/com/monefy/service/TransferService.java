package com.monefy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.monefy.entity.Transfer;
import com.monefy.repository.TransferRepository;

@Service
public class TransferService {

    @Autowired
    private TransferRepository transferRepository;

    public List<Transfer> getAllTransfers() {
        return transferRepository.findAll();
    }

    public Optional<Transfer> getTransferById(Long id) {
        return transferRepository.findById(id);
    }

    public Transfer saveTransfer(Transfer transfer) {
        return transferRepository.save(transfer);
    }

    public void deleteTransfer(Long id) {
        transferRepository.deleteById(id);
    }
}