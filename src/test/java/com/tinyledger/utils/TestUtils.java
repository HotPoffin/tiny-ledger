package com.tinyledger.utils;

import com.tinyledger.domain.transaction.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TestUtils {
    public static final UUID ACCOUNT_ID = UUID.randomUUID();
    public static final String TRANSACTION_TIME_STRING = "2026-05-24T15:00:00Z";
    public static final Instant TRANSACTION_TIME = Instant.parse(TRANSACTION_TIME_STRING);
    public static final Transaction TRANSACTION_1_5 =
        new Transaction(UUID.randomUUID(), TRANSACTION_TIME, BigDecimal.valueOf(1.5));
    public static final Transaction TRANSACTION_MINUS_1_5 =
        new Transaction(UUID.randomUUID(), TRANSACTION_TIME, BigDecimal.valueOf(-1.5));
    public static final Transaction TRANSACTION_MINUS_5_65 =
        new Transaction(UUID.randomUUID(), TRANSACTION_TIME, BigDecimal.valueOf(5.65));
}
