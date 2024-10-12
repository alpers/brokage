package com.brokage.model;

import java.util.stream.Stream;

public enum OrderSide {
    BUY, SELL;

    public static OrderSide fromValue(String value) {
        return Stream.of(values())
                .filter(side -> side.name().equals(value))
                .findFirst()
                .orElse(null);
    }
}
