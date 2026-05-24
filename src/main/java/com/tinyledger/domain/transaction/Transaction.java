package com.tinyledger.domain.transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Transaction(
        UUID transactionId,
        Instant transactionTime,
        BigDecimal amount) {

    public static Transaction fromDao(TransactionDao dao) {
        return new Transaction(dao.transactionId(), dao.transactionTime(), dao.amount());
    }
}
