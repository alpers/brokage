package com.brokage.service;

import com.brokage.entity.Asset;
import com.brokage.entity.Customer;
import com.brokage.entity.Order;
import com.brokage.exception.AssetNotFoundException;
import com.brokage.exception.CustomerNotFoundException;
import com.brokage.exception.InsufficientBalanceException;
import com.brokage.exception.InvalidOperationException;
import com.brokage.model.OrderDTO;
import com.brokage.model.OrderSide;
import com.brokage.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Optional<Customer> findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    public Optional<Customer> findById(long id) {
        return customerRepository.findById(id);
    }

    public void updateUsername(String customerName, String username) {
        Customer customer = findByUsername(customerName).orElseThrow(() -> new CustomerNotFoundException(customerName));
        customer.setUsername(username);
        customerRepository.save(customer);
    }


    public void updatePassword(String customerName, String password) {
        Customer customer = findByUsername(customerName).orElseThrow(() -> new CustomerNotFoundException(customerName));
        customer.setPassword(password);
        customerRepository.save(customer);
    }
}
