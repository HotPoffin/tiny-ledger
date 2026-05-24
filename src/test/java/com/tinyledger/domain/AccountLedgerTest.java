package com.tinyledger.domain;

import com.tinyledger.domain.transaction.Transaction;
import com.tinyledger.exceptions.InvalidTransactionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.tinyledger.utils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountLedgerTest {

    AccountLedger account;

    @BeforeEach
    void setUp() {
        account = new AccountLedger();
    }

    @Test
    void newAccountsAreInitialisedWithZeroValueAndNoTransactions() {
        assertEquals(
            0,
            account.getBalance().compareTo(BigDecimal.ZERO));
    }

    @Test
    void transactionsThrowIfBalanceWouldBeNegative() {
        assertThrows(
            InvalidTransactionException.class,
            () -> account.performTransaction(TRANSACTION_MINUS_1_5));
    }

    @Test
    void positiveTransactionsAddToBalance() {
        assertEquals(
            0,
            account.getBalance().compareTo(BigDecimal.ZERO));
        account.performTransaction(TRANSACTION_1_5);
        assertEquals(
            0,
            account.getBalance().compareTo(BigDecimal.valueOf(1.5)));
    }

    @Test
    void negativeTransactionsRemoveFromBalance() {
        account.balance = BigDecimal.valueOf(1.5);

        assertEquals(
            0,
            account.getBalance().compareTo(BigDecimal.valueOf(1.5)));
        account.performTransaction(TRANSACTION_MINUS_1_5);
        assertEquals(
            0,
            account.getBalance().compareTo(BigDecimal.ZERO));
    }

    @Test
    void replayingTransactionDoesNotDuplicateTransaction() {
        account.performTransaction(TRANSACTION_1_5);

        assertEquals(BigDecimal.valueOf(1.5), account.balance);
        assertThat(account.transactions.keySet())
            .containsExactly(TRANSACTION_1_5.transactionId());

        account.performTransaction(TRANSACTION_1_5);

        assertEquals(BigDecimal.valueOf(1.5), account.balance);
        assertThat(account.transactions.keySet())
            .containsExactly(TRANSACTION_1_5.transactionId());
    }

    @Test
    void getBalanceReturnsBalanceBalue() {
        BigDecimal result = account.getBalance();

        assertEquals(0, result.compareTo(BigDecimal.ZERO));
    }

    @Test
    void getTransactionHistoryReturnsEmptyForNewAccount() {
        List<Transaction> result = account.getTransactionHistory();

        assertEquals(0, result.size());
    }

    @Test
    void getTransactionHistoryReturnsAllTransactionsInOrder() {
        account.performTransaction(TRANSACTION_1_5);
        account.performTransaction(TRANSACTION_MINUS_1_5);
        account.performTransaction(TRANSACTION_MINUS_5_65);

        List<Transaction> result = account.getTransactionHistory();

        assertThat(result)
            .usingRecursiveFieldByFieldElementComparatorIgnoringFields("transactionTime", "previousTransaction")
            .containsExactly(TRANSACTION_MINUS_5_65, TRANSACTION_MINUS_1_5, TRANSACTION_1_5);
    }
}
