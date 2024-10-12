package com.brokage.service;

import com.brokage.entity.Asset;
import com.brokage.entity.Customer;
import com.brokage.entity.Order;
import com.brokage.exception.*;
import com.brokage.model.OrderDTO;
import com.brokage.model.OrderSide;
import com.brokage.model.OrderStatus;
import com.brokage.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CustomerService customerService;
    private final AssetService assetService;
    private final OrderRepository orderRepository;

    public Order createOrder(OrderDTO orderDto) {
        Customer customer = customerService.findById(orderDto.getCustomer())
                .orElseThrow(() -> new CustomerNotFoundException(orderDto.getCustomer().toString()));
        Order order = buildOrder(orderDto);
        double totalCost = order.getPrice() * order.getSize();
        String defaultAsset = assetService.getDefaultAsset();

        if (OrderSide.BUY.name().equals(order.getOrderSide())) {
            Asset tryAsset = assetService.findByCustomerIdAndAssetName(customer.getId(), defaultAsset)
                    .orElse(assetService.buildAsset(order.getCustomerId(), defaultAsset));

            if (tryAsset.getUsableSize() < totalCost) {
                throw new InsufficientBalanceException(tryAsset.getAssetName());
            }

            tryAsset.setUsableSize(tryAsset.getUsableSize() - totalCost);
            assetService.saveAsset(tryAsset);
        } else if (OrderSide.SELL.name().equals(order.getOrderSide())) {
            Asset assetToSell = assetService.findByCustomerIdAndAssetName(customer.getId(), order.getAssetName())
                    .orElseThrow(() -> new AssetNotFoundException(customer.getId(), order.getAssetName()));

            if (assetToSell.getUsableSize() < order.getSize()) {
                throw new InsufficientBalanceException(order.getAssetName());
            }

            assetToSell.setUsableSize(assetToSell.getUsableSize() - order.getSize());
            assetService.saveAsset(assetToSell);
        } else {
            throw new InvalidOperationException("Invalid order side: must be BUY or SELL");
        }

        return saveOrder(order);
    }

    public List<OrderDTO> listOrders(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByCustomerIdAndCreatedDateBetween(customerId, startDate, endDate).stream()
                .map(this::mapToOrderDto).collect(Collectors.toList());
    }

    public List<OrderDTO> listCustomerOrders(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream().map(this::mapToOrderDto).collect(Collectors.toList());
    }

    public List<OrderDTO> listOrdersByCustomer(String customerName) {
        return customerService.findByUsername(customerName)
                .map(value -> orderRepository.findByCustomerId(value.getId()).stream().map(this::mapToOrderDto).toList())
                .orElseGet(List::of);
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        String defaultAsset = assetService.getDefaultAsset();
        if (!order.getStatus().equals(OrderStatus.PENDING.name())) {
            throw new InvalidOperationException("Order other than PENDING cannot be canceled.");
        }

        if (OrderSide.BUY.name().equals(order.getOrderSide())) {
            Asset boughtAsset = assetService.findByCustomerIdAndAssetName(order.getCustomerId(), defaultAsset)
                    .orElse(assetService.buildAsset(order.getCustomerId(), defaultAsset));

            double refund = order.getPrice() * order.getSize();
            boughtAsset.setUsableSize(boughtAsset.getUsableSize() + refund);

            assetService.saveAsset(boughtAsset);
        } else if (OrderSide.SELL.name().equals(order.getOrderSide())) {
            Asset asset = assetService.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName())
                    .orElseThrow(() -> new AssetNotFoundException(order.getCustomerId(), order.getAssetName()));

            asset.setUsableSize(asset.getUsableSize() + order.getSize());
            assetService.saveAsset(asset);
        } else {
            throw new InvalidOperationException("Invalid order side: must be BUY or SELL");
        }

        order.setStatus(OrderStatus.CANCELED.name());
        saveOrder(order);
    }

    public void matchPendingOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!OrderStatus.PENDING.name().equals(order.getStatus())) {
            throw new InvalidOperationException("Order other than PENDING cannot be canceled.");
        }

        double totalCost = order.getPrice() * order.getSize();
        String defaultAsset = assetService.getDefaultAsset();

        if (OrderSide.BUY.name().equals(order.getOrderSide())) {
            Asset tryAsset = assetService.findByCustomerIdAndAssetName(order.getCustomerId(), defaultAsset)
                    .orElseThrow(() -> new AssetNotFoundException(order.getCustomerId(), defaultAsset));

            if (tryAsset.getSize() < totalCost) {
                throw new InsufficientBalanceException(defaultAsset);
            }

            tryAsset.setSize(tryAsset.getSize() - totalCost);
            assetService.saveAsset(tryAsset);

            Asset boughtAsset = assetService.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName())
                    .orElse(assetService.buildAsset(order.getCustomerId(), order.getAssetName()));
            boughtAsset.setSize(boughtAsset.getSize() + order.getSize());
            boughtAsset.setUsableSize(boughtAsset.getUsableSize() + order.getSize());
            assetService.saveAsset(boughtAsset);
        } else if (OrderSide.SELL.name().equals(order.getOrderSide())) {
            Asset assetToSell = assetService.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName())
                    .orElseThrow(() -> new AssetNotFoundException(order.getCustomerId(), order.getAssetName()));

            if (assetToSell.getSize() < order.getSize()) {
                throw new InsufficientBalanceException(order.getAssetName());
            }

            assetToSell.setSize(assetToSell.getUsableSize() - order.getSize());
            assetService.saveAsset(assetToSell);

            Asset tryAsset = assetService.findByCustomerIdAndAssetName(order.getCustomerId(), defaultAsset)
                    .orElse(assetService.buildAsset(order.getCustomerId(), defaultAsset));

            tryAsset.setSize(tryAsset.getSize() + totalCost);
            tryAsset.setUsableSize(tryAsset.getUsableSize() + totalCost);
            assetService.saveAsset(tryAsset);
        }

        order.setStatus(OrderStatus.MATCHED.name());
        saveOrder(order);
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public OrderDTO mapToOrderDto(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .customer(order.getCustomerId())
                .asset(order.getAssetName())
                .side(OrderSide.fromValue(order.getOrderSide()))
                .size(order.getSize())
                .price(order.getPrice())
                .status(OrderStatus.fromValue(order.getStatus()))
                .createdDate(order.getCreatedDate())
                .build();
    }

    public Order buildOrder(OrderDTO orderDto) {
        return Order.builder()
                .customerId(orderDto.getCustomer())
                .assetName(orderDto.getAsset())
                .orderSide(orderDto.getSide().name())
                .size(orderDto.getSize())
                .price(orderDto.getPrice())
                .status(OrderStatus.PENDING.name())
                .createdDate(LocalDateTime.now())
                .build();
    }
}
