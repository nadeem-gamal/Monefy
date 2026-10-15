package com.monefy.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.monefy.entity.Transfer;
import com.monefy.exception.BusinessRuleException;
import com.monefy.service.TransferService;

@RestController
@RequestMapping("/transfers")
public class TransferController {

    @Autowired
    private TransferService transferService;

    // All three query params are optional and can be combined freely; accountId matches either side.
    @GetMapping
    public List<Transfer> getAllTransfers(
            @RequestParam(required = false) Integer accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return transferService.getTransfers(accountId, from, to);
    }

    @GetMapping("/{id}")
    public Optional<Transfer> getTransferById(@PathVariable Long id) {
        return transferService.getTransferById(id);
    }

    @PostMapping
    public Transfer createTransfer(@RequestBody Transfer transfer) {
        transfer.setId(null);
        return transferService.saveTransfer(transfer);
    }

    @PutMapping
    public Transfer updateTransfer(@RequestBody Transfer transfer) {
        if (transfer.getId() == null) {
            throw new BusinessRuleException("Transfer id is required for an update");
        }
        return transferService.saveTransfer(transfer);
    }

    @DeleteMapping("/{id}")
    public void deleteTransfer(@PathVariable Long id) {
        transferService.deleteTransfer(id);
    }
}
