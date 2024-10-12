package com.brokage.controller;

import com.brokage.config.JwtTokenProvider;
import com.brokage.model.OrderDTO;
import com.brokage.model.OrderSide;
import com.brokage.model.OrderStatus;
import com.brokage.service.AssetService;
import com.brokage.service.CustomerService;
import com.brokage.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private AssetService assetService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testListOrdersSuccess() throws Exception {
        // Arrange
        Long customerId = 1L;
        LocalDateTime start = LocalDateTime.now().minusDays(1L);
        LocalDateTime end = LocalDateTime.now();

        List<OrderDTO> orders = List.of(
                OrderDTO.builder()
                        .customer(customerId)
                        .asset("ING")
                        .side(OrderSide.BUY)
                        .size(10.0)
                        .price(150.0)
                        .status(OrderStatus.PENDING)
                        .build());

        when(orderService.listOrders(customerId, start, end)).thenReturn(orders);

        // Act
        MvcResult result = mockMvc.perform(
                        get("/api/order/orders?customerId={id}&startDate={start}&endDate={end}",
                                customerId, start, end)
                                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        OrderDTO[] orderArray = objectMapper.readValue(jsonResponse, OrderDTO[].class);

        //Assert
        assertNotNull(orderArray);
        assertEquals(1, orderArray.length);
        assertEquals("ING", orderArray[0].getAsset());
        assertEquals(OrderSide.BUY, orderArray[0].getSide());
    }
}
