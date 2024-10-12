package com.brokage.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String assetName) {
        super("Insufficient " + assetName + " balance.");
    }
}
