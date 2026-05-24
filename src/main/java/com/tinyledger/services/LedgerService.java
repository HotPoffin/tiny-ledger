package com.tinyledger.services;

import com.tinyledger.domain.AccountLedger;
import com.tinyledger.domain.transaction.Transaction;
import com.tinyledger.exceptions.InvalidAccountException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class LedgerService {
    Map<UUID, AccountLedger> accountLedgers;

    public LedgerService() {
        this.accountLedgers = new HashMap<>();
    }

    public UUID createAccount() {
        UUID accountId = UUID.randomUUID();
        accountLedgers.put(accountId, new AccountLedger());
        return accountId;
    }

    public UUID performTransaction(UUID accountId,
                                   Transaction transaction) {
        getAccountLedger(accountId)
            .performTransaction(transaction);
        return transaction.transactionId();
    }

    public BigDecimal getAccountBalance(UUID accountId) {
        return getAccountLedger(accountId)
            .getBalance();
    }

    public List<Transaction> getTransactionHistory(UUID accountId) {
        return getAccountLedger(accountId)
            .getTransactionHistory();
    }

    private AccountLedger getAccountLedger(UUID accountId) {
        return Optional.ofNullable(accountLedgers.getOrDefault(accountId, null))
            .orElseThrow(() -> new InvalidAccountException(accountId));
    }
}
