package com.tinyledger.domain;

import com.tinyledger.domain.transaction.Transaction;
import com.tinyledger.domain.transaction.TransactionDao;
import com.tinyledger.exceptions.InvalidTransactionException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

public class AccountLedger {
    BigDecimal balance;
    UUID lastTransaction;
    Map<UUID, TransactionDao> transactions;

    public AccountLedger() {
        this.balance = BigDecimal.ZERO;
        this.lastTransaction = null;
        this.transactions = new HashMap<>();
    }

    public void performTransaction(Transaction transaction) {
        if (!isTransactionValid(transaction.amount())) {
            throw new InvalidTransactionException(transaction.transactionId());
        }

        transactions.computeIfAbsent(
            transaction.transactionId(),
            (unused) -> updateLedger(transaction));
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<Transaction> getTransactionHistory() {
        if (Objects.isNull(lastTransaction)) {
            return Collections.emptyList();
        }

        return Stream
            .iterate(
                transactions.get(lastTransaction),
                Objects::nonNull,
                (transaction) -> transactions.get(transaction.previousTransaction()))
            .map(Transaction::fromDao)
            .toList();
    }

    private boolean isTransactionValid(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) >= 0 ||
            amount.abs().compareTo(balance) <= 0;
    }

    private TransactionDao updateLedger(Transaction transaction) {
        UUID previousTransaction = lastTransaction;
        lastTransaction = transaction.transactionId();
        balance = balance.add(transaction.amount());
        return new TransactionDao(
            transaction.transactionId(),
            previousTransaction,
            transaction.transactionTime(),
            transaction.amount());
    }
}
