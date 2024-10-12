package com.brokage.exception;

public class AssetNotFoundException extends RuntimeException {
    public AssetNotFoundException(Long customerId, String assetName) {
        super(assetName + " asset for customer with ID " + customerId + " not found.");
    }
}
