package com.tinyledger.domain.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public record TransactionDto(
    UUID transactionId,
    Optional<Instant> transactionTime,
    @DecimalMin(value = "0.00", inclusive = false)
    @Digits(integer = 7, fraction = 2)
    BigDecimal amount) {

    public static TransactionDto fromDomain(Transaction transaction) {
        return new TransactionDto(
            transaction.transactionId(),
            Optional.of(transaction.transactionTime()),
            transaction.amount());
    }
}
