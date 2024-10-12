package com.brokage.model;

import java.util.stream.Stream;

public enum OrderStatus {
    PENDING, MATCHED, CANCELED;

    public static OrderStatus fromValue(String value) {
        return Stream.of(values())
                .filter(status -> status.name().equals(value))
                .findFirst()
                .orElse(null);
    }
}
