package com.brokage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ORDERS")
public class Order {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "asset_name")
    private String assetName;

    @Column(name = "order_side")
    private String orderSide;

    @Column(name = "order_size")
    private Double size;

    @Column(name = "price")
    private Double price;

    @Column(name = "order_status")
    private String status;

    @Column(name = "created_date")
    private LocalDateTime createdDate;
}
