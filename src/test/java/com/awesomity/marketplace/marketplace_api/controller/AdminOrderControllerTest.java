package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.AdminOrderDto;
import com.awesomity.marketplace.marketplace_api.dto.ApiResponse;
import com.awesomity.marketplace.marketplace_api.entity.Order;
import com.awesomity.marketplace.marketplace_api.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AdminOrderControllerTest {

    @InjectMocks
    private AdminOrderController controller;

    @Mock
    private OrderService orderService;

    private AdminOrderDto testAdminDto;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testAdminDto = new AdminOrderDto(
                1L,
                "Amies Guiella",
                "tstawesome@gmail.com",
                "PLACED",
                "SUCCESS",
                "CREDIT_CARD",
                100.0,
                LocalDateTime.now()
        );

        testOrder = new Order();
        testOrder.setId(1L);
    }

    @Test
    void shouldReturnAllOrders() {
        when(orderService.findAll()).thenReturn(Collections.singletonList(testAdminDto));

        ResponseEntity<ApiResponse<List<AdminOrderDto>>> response = controller.getAllOrders();

        assertThat(response.getBody().getData()).contains(testAdminDto);
    }

    @Test
    void shouldReturnOrderById() {
        when(orderService.findById(1L)).thenReturn(testOrder);

        ResponseEntity<ApiResponse<Order>> response = controller.getOrderById(1L);

        assertThat(response.getBody().getData()).isEqualTo(testOrder);
    }

    @Test
    void shouldUpdateOrderStatus() {
        when(orderService.updateOrderStatus(1L, "SHIPPED")).thenReturn(testOrder);

        ResponseEntity<ApiResponse<Order>> response = controller.updateOrderStatus(1L, "SHIPPED");

        assertThat(response.getBody().getData()).isEqualTo(testOrder);
    }

    @Test
    void shouldDeleteOrder() {
        doNothing().when(orderService).deleteOrder(1L);

        ResponseEntity<ApiResponse<Void>> response = controller.deleteOrder(1L);

        assertThat(response.getBody().getMessage()).isEqualTo("Order deleted");
        verify(orderService).deleteOrder(1L);
    }
}
