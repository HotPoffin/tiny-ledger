package com.tinyledger.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidTransactionException extends RuntimeException {
    public InvalidTransactionException(UUID transactionId) {
        super(String.format("Transaction %s has been rejected as it would result in a below zero balance", transactionId));
    }
}
