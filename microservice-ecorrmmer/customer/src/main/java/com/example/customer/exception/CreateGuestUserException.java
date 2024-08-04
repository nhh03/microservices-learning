package com.example.customer.exception;

public class CreateGuestUserException extends  RuntimeException {
    public CreateGuestUserException(final String message) {
        super(message);
    }
}
