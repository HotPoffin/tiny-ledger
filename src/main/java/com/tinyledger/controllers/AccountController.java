package com.tinyledger.controllers;

import com.tinyledger.domain.transaction.Transaction;
import com.tinyledger.domain.transaction.TransactionDto;
import com.tinyledger.services.LedgerService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/v1/accounts",
    produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {
    private final LedgerService ledgerService;

    public AccountController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @PostMapping("/create")
    public UUID createAccount() {
        return ledgerService.createAccount();
    }

    @PostMapping(value = "/{accountId}/deposit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UUID deposit(@PathVariable UUID accountId,
                        @Valid @RequestBody TransactionDto transaction) {
        return ledgerService.performTransaction(
            accountId,
            new Transaction(
                transaction.transactionId(),
                transaction.transactionTime().orElse(Instant.now()),
                transaction.amount()));
    }

    @PostMapping(value = "/{accountId}/withdraw", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UUID withdraw(@PathVariable UUID accountId,
                         @Valid @RequestBody TransactionDto transaction) {
        return ledgerService.performTransaction(
            accountId,
            new Transaction(
                transaction.transactionId(),
                transaction.transactionTime().orElse(Instant.now()),
                transaction.amount().negate()));
    }

    @GetMapping("/{accountId}/balance")
    public BigDecimal getBalance(@PathVariable UUID accountId) {
        return ledgerService.getAccountBalance(accountId);
    }

    @GetMapping("/{accountId}/transaction-history")
    public List<TransactionDto> getTransactionHistory(@PathVariable UUID accountId) {
        return ledgerService.getTransactionHistory(accountId).stream()
            .map(TransactionDto::fromDomain)
            .toList();
    }
}
