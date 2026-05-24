package com.tinyledger.controllers;

import com.tinyledger.domain.transaction.Transaction;
import com.tinyledger.services.LedgerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.tinyledger.utils.TestUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class AccountControllerTest {
    static final String TRANSACTION_DTO = """
        {
            "transactionId": "%s",
            "transactionTime": "%s",
            "amount": %s
        }
    """;
    static final String NO_TIME_TRANSACTION_DTO = """
        {
            "transactionId": "%s",
            "amount": %s
        }
    """;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    LedgerService ledgerService;

    @Test
    void whenCreateAccount_returnsAccountId() throws Exception {
        when(ledgerService.createAccount())
            .thenReturn(ACCOUNT_ID);

        mockMvc
            .perform(post("/v1/accounts/create"))
            .andExpect(status().isOk())
            .andExpect(content().json(String.format("\"%s\"", ACCOUNT_ID)));
    }

    @Test
    void whenSuccessfulDeposit_returnsTransactionId() throws Exception {
        when(ledgerService.performTransaction(ACCOUNT_ID, TRANSACTION_1_5))
            .thenReturn(TRANSACTION_1_5.transactionId());

        mockMvc
            .perform(post("/v1/accounts/{accountId}/deposit", ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(String.format(TRANSACTION_DTO, TRANSACTION_1_5.transactionId(), TRANSACTION_TIME_STRING, "1.5")))
            .andExpect(status().isOk())
            .andExpect(content().json(String.format("\"%s\"", TRANSACTION_1_5.transactionId().toString())));
    }

    @Test
    void whenDepositWithNoTransactionTime_succeedsAndReturnsTransactionId() throws Exception {
        when(ledgerService.performTransaction(eq(ACCOUNT_ID), any(Transaction.class)))
            .thenReturn(TRANSACTION_1_5.transactionId());

        mockMvc
            .perform(post("/v1/accounts/{accountId}/deposit", ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(String.format(NO_TIME_TRANSACTION_DTO, TRANSACTION_1_5.transactionId(), "10")))
            .andExpect(status().isOk())
            .andExpect(content().json(String.format("\"%s\"", TRANSACTION_1_5.transactionId().toString())));
    }

    @Test
    void whenDepositWithInvalidAccountId_returnsBadRequest() throws Exception {
        mockMvc
            .perform(post("/v1/accounts/{accountId}/deposit", "invalid")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(String.format(TRANSACTION_DTO, UUID.randomUUID(), TRANSACTION_TIME_STRING, "10")))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenDepositWithInvalidTransactionId_returnsBadRequest() throws Exception {
        mockMvc
            .perform(post("/v1/accounts/{accountId}/deposit", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(String.format(TRANSACTION_DTO,  "invalid", TRANSACTION_TIME_STRING, "10")))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenDepositWithNegativeAmount_returnsBadRequest() throws Exception {
        mockMvc
            .perform(post("/v1/accounts/{accountId}/deposit", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(String.format(TRANSACTION_DTO, UUID.randomUUID(), TRANSACTION_TIME_STRING, "-10")))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenDepositWithTooMuchPrecision_returnsBadRequest() throws Exception {
        mockMvc
            .perform(post("/v1/accounts/{accountId}/deposit", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(String.format(TRANSACTION_DTO, UUID.randomUUID(), TRANSACTION_TIME_STRING, "1.001")))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenDepositWithGreaterThanConfiguredLimit_returnsBadRequest() throws Exception {
        mockMvc
            .perform(post("/v1/accounts/{accountId}/deposit", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(String.format(TRANSACTION_DTO, UUID.randomUUID(), TRANSACTION_TIME_STRING, "10000000.00")))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenSuccessfulWithdrawal_returnsTransactionId() throws Exception {
        when(ledgerService.performTransaction(ACCOUNT_ID, TRANSACTION_MINUS_1_5))
            .thenReturn(TRANSACTION_MINUS_1_5.transactionId());

        mockMvc
            .perform(post("/v1/accounts/{accountId}/withdraw", ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(String.format(TRANSACTION_DTO, TRANSACTION_MINUS_1_5.transactionId(), TRANSACTION_TIME_STRING, "1.5")))
            .andExpect(status().isOk())
            .andExpect(content().json(String.format("\"%s\"", TRANSACTION_MINUS_1_5.transactionId().toString())));
    }

    @Test
    void whenWithdrawalWithNoTransactionTime_succeedsAndReturnsTransactionId() throws Exception {
        when(ledgerService.performTransaction(eq(ACCOUNT_ID), any(Transaction.class)))
            .thenReturn(TRANSACTION_1_5.transactionId());

        mockMvc
            .perform(post("/v1/accounts/{accountId}/withdraw", ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(String.format(NO_TIME_TRANSACTION_DTO, TRANSACTION_1_5.transactionId(), "10")))
            .andExpect(status().isOk())
            .andExpect(content().json(String.format("\"%s\"", TRANSACTION_1_5.transactionId().toString())));
    }

    @Test
    void whenWithdrawWithInvalidAccountId_returnsBadRequest() throws Exception {
        mockMvc
            .perform(post("/v1/accounts/{accountId}/withdraw", "invalid")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(String.format(TRANSACTION_DTO, UUID.randomUUID(), TRANSACTION_TIME_STRING, "10")))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenWithdrawWithInvalidTransactionId_returnsBadRequest() throws Exception {
        mockMvc
            .perform(post("/v1/accounts/{accountId}/withdraw", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(String.format(TRANSACTION_DTO, "invalid", TRANSACTION_TIME_STRING, "10")))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenWithdrawWithNegativeAmount_returnsBadRequest() throws Exception {
        mockMvc
            .perform(post("/v1/accounts/{accountId}/withdraw", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(String.format(TRANSACTION_DTO, UUID.randomUUID(), TRANSACTION_TIME_STRING, "-10")))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenWithdrawWithTooMuchPrecision_returnsBadRequest() throws Exception {
        mockMvc
            .perform(post("/v1/accounts/{accountId}/withdraw", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(String.format(TRANSACTION_DTO, UUID.randomUUID(), TRANSACTION_TIME_STRING, "1.001")))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenWithdrawWithGreaterThanConfiguredLimit_returnsBadRequest() throws Exception {
        mockMvc
            .perform(post("/v1/accounts/{accountId}/withdraw", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(String.format(TRANSACTION_DTO, UUID.randomUUID(), TRANSACTION_TIME_STRING, "10000000.00")))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetBalance_returnsBalance() throws Exception {
        when(ledgerService.getAccountBalance(ACCOUNT_ID))
            .thenReturn(BigDecimal.TEN);

        mockMvc
            .perform(get("/v1/accounts/{accountId}/balance", ACCOUNT_ID))
            .andExpect(status().isOk())
            .andExpect(content().json("10"));
    }

    @Test
    void whenGetBalanceWithInvalidAccountId_returnsBadRequest() throws Exception {
        mockMvc
            .perform(get("/v1/accounts/{accountId}/balance", "invalid"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenTransactionHistory_returnsTransactionLog() throws Exception {
        when(ledgerService.getTransactionHistory(ACCOUNT_ID))
            .thenReturn(List.of(TRANSACTION_MINUS_1_5, TRANSACTION_1_5));

        mockMvc
            .perform(get("/v1/accounts/{accountId}/transaction-history", ACCOUNT_ID))
            .andExpect(status().isOk())
            .andExpect(content().json(
                String.format("[%s, %s]",
                    String.format(TRANSACTION_DTO, TRANSACTION_MINUS_1_5.transactionId(), TRANSACTION_TIME_STRING, -1.5),
                    String.format(TRANSACTION_DTO, TRANSACTION_1_5.transactionId(), TRANSACTION_TIME_STRING, 1.5))));
    }

    @Test
    void whenGetTransactionHistoryWithInvalidAccountId_returnsBadRequest() throws Exception {
        mockMvc
            .perform(get("/v1/accounts/{accountId}/transaction-history", "invalid"))
            .andExpect(status().isBadRequest());
    }
}
