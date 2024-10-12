package com.brokage.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String customer) {
        super("Customer (" + customer + ") not found.");
    }
}
