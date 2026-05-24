package com.tinyledger.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidAccountException extends RuntimeException {
    public InvalidAccountException(UUID accountId) {
        super(String.format("Account with ID %s does not exist", accountId));
    }
}
