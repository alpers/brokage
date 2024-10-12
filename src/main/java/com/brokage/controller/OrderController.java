package com.brokage.controller;

import com.brokage.entity.Asset;
import com.brokage.entity.Order;
import com.brokage.model.AssetDTO;
import com.brokage.model.OrderDTO;
import com.brokage.service.AssetService;
import com.brokage.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AssetService assetService;

    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody @Validated OrderDTO order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> listOrders(@RequestParam Long customerId,
                                                     @RequestParam LocalDateTime startDate,
                                                     @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(orderService.listOrders(customerId, startDate, endDate));
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deposit")
    public ResponseEntity<Void> depositMoney(@RequestBody @Validated AssetDTO assetDTO) {
        assetService.depositMoney(assetDTO.getCustomerId(), assetDTO.getAmount());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdrawMoney(@RequestBody @Validated AssetDTO assetDTO) throws Exception {
        assetService.withdrawMoney(assetDTO.getCustomerId(), assetDTO.getAmount());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/assets/{customerId}")
    public ResponseEntity<List<Asset>> listAssets(@PathVariable Long customerId) {
        return ResponseEntity.ok(assetService.listAssets(customerId));
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<List<OrderDTO>> listCustomerOrders(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.listCustomerOrders(customerId));
    }

    @PostMapping("/match")
    public ResponseEntity<String> matchOrder(@RequestParam Long orderId) {
        orderService.matchPendingOrder(orderId);
        return ResponseEntity.ok("Order matched successfully.");
    }
}
