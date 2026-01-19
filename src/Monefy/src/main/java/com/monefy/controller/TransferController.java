package com.monefy.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monefy.entity.Transfer;
import com.monefy.service.TransferService;

@RestController
@RequestMapping("/transfers")
public class TransferController {

    @Autowired
    private TransferService transferService;

    @GetMapping
    public List<Transfer> getAllTransfers() {
        return transferService.getAllTransfers();
    }

    @GetMapping("/{id}")
    public Optional<Transfer> getTransferById(@PathVariable Long id) {
        return transferService.getTransferById(id);
    }

    @PostMapping
    public Transfer createTransfer(@RequestBody Transfer transfer) {
        return transferService.saveTransfer(transfer);
    }

    @DeleteMapping("/{id}")
    public void deleteTransfer(@PathVariable Long id) {
        transferService.deleteTransfer(id);
    }
}