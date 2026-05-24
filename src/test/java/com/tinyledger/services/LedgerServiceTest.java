package com.tinyledger.services;

import com.tinyledger.domain.AccountLedger;
import com.tinyledger.domain.transaction.Transaction;
import com.tinyledger.exceptions.InvalidAccountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.tinyledger.utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LedgerServiceTest {

    private LedgerService ledgerService;
    @Mock
    private AccountLedger accountLedger;

    @BeforeEach
    void setUp() {
        ledgerService = new LedgerService();
    }

    @Test
    void ledgerServiceInitialisesWithNoInitialLedgers() {
        assertTrue(ledgerService.accountLedgers.isEmpty());
    }

    @Test
    void createAccountCreatesNewLedger() {
        UUID accountId = ledgerService.createAccount();

        assertEquals(1, ledgerService.accountLedgers.size());
        assertTrue(ledgerService.accountLedgers.containsKey(accountId));
    }

    @Test
    void performTransactionThrowsExceptionForInvalidAccountIds() {
        assertThrows(
            InvalidAccountException.class,
            () -> ledgerService.performTransaction(UUID.randomUUID(), TRANSACTION_1_5));
    }

    @Test
    void performTransactionSucceedsForValidAccountIds() {
        ledgerService.accountLedgers.put(ACCOUNT_ID, accountLedger);

        UUID result = ledgerService.performTransaction(ACCOUNT_ID, TRANSACTION_1_5);

        assertEquals(result, TRANSACTION_1_5.transactionId());
        verify(accountLedger)
            .performTransaction(TRANSACTION_1_5);
    }

    @Test
    void getAccountBalanceThrowsForInvalidAccountId() {
        assertThrows(
            InvalidAccountException.class,
            () -> ledgerService.getAccountBalance(UUID.randomUUID()));
    }

    @Test
    void getAccountBalanceReturnsAccountBalance() {
        BigDecimal balance = BigDecimal.TEN;
        when(accountLedger.getBalance())
            .thenReturn(balance);
        ledgerService.accountLedgers.put(ACCOUNT_ID, accountLedger);

        BigDecimal result = ledgerService.getAccountBalance(ACCOUNT_ID);

        assertEquals(balance, result);
        verify(accountLedger)
            .getBalance();
    }

    @Test
    void getTransactionHistoryThrowsForInvalidAccountId() {
        assertThrows(
            InvalidAccountException.class,
            () -> ledgerService.getTransactionHistory(UUID.randomUUID()));
    }

    @Test
    void getTransactionHistoryReturnsTransactionHistory() {
        List<Transaction> transactionHistory = List.of(TRANSACTION_MINUS_1_5, TRANSACTION_1_5);
        when(accountLedger.getTransactionHistory())
            .thenReturn(transactionHistory);
        ledgerService.accountLedgers.put(ACCOUNT_ID, accountLedger);

        List<Transaction> result = ledgerService.getTransactionHistory(ACCOUNT_ID);

        assertEquals(transactionHistory, result);
        verify(accountLedger)
            .getTransactionHistory();
    }
}
