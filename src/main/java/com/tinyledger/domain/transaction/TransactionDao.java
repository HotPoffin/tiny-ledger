package com.tinyledger.domain.transaction;

import jakarta.annotation.Nullable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionDao(
    UUID transactionId,
    @Nullable UUID previousTransaction,
    Instant transactionTime,
    BigDecimal amount) {
}
