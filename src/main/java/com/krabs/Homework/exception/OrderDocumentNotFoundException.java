package com.krabs.Homework.exception;

public class OrderDocumentNotFoundException extends RuntimeException {
    public OrderDocumentNotFoundException(String message) {
        super(message);
    }
    public OrderDocumentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
