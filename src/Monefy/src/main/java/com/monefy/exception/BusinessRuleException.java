package com.monefy.exception;

/**
 * Thrown when a request is well-formed but violates a business rule
 * (e.g. zero amount, transfer to the same account, deleting an account that still has transactions).
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }

    public BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}
