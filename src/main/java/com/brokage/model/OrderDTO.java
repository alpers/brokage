package com.brokage.model;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    @Nullable
    private Long id;

    @NotNull
    private OrderSide side;

    @PositiveOrZero
    private Long customer;

    @NotBlank
    private String asset;

    @Positive
    private Double size;

    @Positive
    private Double price;

    @Nullable
    private OrderStatus status;

    @Nullable
    private LocalDateTime createdDate;
}
