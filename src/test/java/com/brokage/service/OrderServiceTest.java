package com.brokage.service;

import com.brokage.entity.Asset;
import com.brokage.entity.Customer;
import com.brokage.entity.Order;
import com.brokage.exception.CustomerNotFoundException;
import com.brokage.exception.InsufficientBalanceException;
import com.brokage.model.OrderDTO;
import com.brokage.model.OrderSide;
import com.brokage.model.OrderStatus;
import com.brokage.repository.AssetRepository;
import com.brokage.repository.CustomerRepository;
import com.brokage.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private CustomerService customerService;

    @Mock
    private AssetService assetService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Test
    public void testCreateBuyOrderSuccess() throws Exception {
        // Arrange
        OrderDTO order = OrderDTO.builder()
                .customer(1L)
                .asset("TRY")
                .side(OrderSide.BUY)
                .size(10.0)
                .price(200.0)
                .status(OrderStatus.PENDING)
                .build();

        Customer customer = new Customer();
        customer.setId(order.getCustomer());
        Asset asset = Asset.builder()
                .assetName("TRY")
                .customerId(order.getCustomer())
                .size(20000.0)
                .usableSize(20000.0)
                .build();

        when(customerService.findById(order.getCustomer())).thenReturn(Optional.of(customer));
        when(assetService.getDefaultAsset()).thenReturn("TRY");
        when(assetService.findByCustomerIdAndAssetName(order.getCustomer(), "TRY"))
                .thenReturn(Optional.of(asset));
        when(orderService.saveOrder(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order result = orderService.createOrder(order);

        // Assert
        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
        assertEquals(asset.getAssetName(), result.getAssetName());
        verify(assetService).saveAsset(asset);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    public void testCreateSellOrderInsufficientAssets() {
        // Arrange
        OrderDTO order = OrderDTO.builder()
                .customer(1L)
                .asset("ING")
                .side(OrderSide.SELL)
                .size(10.0)
                .price(200.0)
                .status(OrderStatus.PENDING)
                .build();

        Customer customer = new Customer();
        customer.setId(order.getCustomer());
        Asset assetToSell = Asset.builder()
                .assetName(order.getAsset())
                .customerId(order.getCustomer())
                .size(10.0)
                .usableSize(5.0)
                .build();

        when(customerService.findById(order.getCustomer())).thenReturn(Optional.of(customer));
        when(assetService.findByCustomerIdAndAssetName(order.getCustomer(), assetToSell.getAssetName()))
                .thenReturn(Optional.of(assetToSell));

        // Act
        InsufficientBalanceException exception =
                assertThrows(InsufficientBalanceException.class, () -> orderService.createOrder(order));

        // Assert
        assertEquals("Insufficient " + assetToSell.getAssetName() + " balance.", exception.getMessage());
        verify(assetService, never()).saveAsset(any(Asset.class));
    }

    @Test
    public void testCreateOrderCustomerNotFound() {
        OrderDTO order = OrderDTO.builder()
                .customer(1L)
                .asset("ING")
                .side(OrderSide.BUY)
                .size(10.0)
                .price(200.0)
                .status(OrderStatus.PENDING)
                .build();

        when(customerService.findById(order.getCustomer())).thenReturn(Optional.empty());

        CustomerNotFoundException exception =
                assertThrows(CustomerNotFoundException.class, () -> orderService.createOrder(order));

        assertEquals("Customer (" + order.getCustomer() + ") not found.", exception.getMessage());
    }
}
