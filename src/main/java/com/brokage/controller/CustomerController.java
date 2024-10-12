package com.brokage.controller;

import com.brokage.model.AuthRequest;
import com.brokage.model.OrderDTO;
import com.brokage.service.CustomerService;
import com.brokage.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final OrderService orderService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> listOrders(Principal principal) {
        String customer = principal.getName();
        List<OrderDTO> orders = orderService.listOrdersByCustomer(customer);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/username")
    public ResponseEntity<Void> updateUsername(Principal principal, @RequestBody AuthRequest request) {
        if (!StringUtils.hasText(request.getUsername())) {
            throw new IllegalArgumentException("Username is invalid.");
        }
        String customer = principal.getName();
        customerService.updateUsername(customer, request.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password")
    public ResponseEntity<Void> updatePassword(Principal principal, @RequestBody AuthRequest request) {
        if (!StringUtils.hasText(request.getPassword())) {
            throw new IllegalArgumentException("Password is invalid.");
        }
        String customer = principal.getName();
        customerService.updatePassword(customer, passwordEncoder.encode(request.getPassword()));
        return ResponseEntity.ok().build();
    }
}
