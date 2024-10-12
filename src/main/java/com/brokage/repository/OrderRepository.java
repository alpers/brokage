package com.brokage.repository;

import com.brokage.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerIdAndCreatedDateBetween(Long customerId, LocalDateTime startDate, LocalDateTime endDate);

    List<Order> findByCustomerId(Long customerId);
}
