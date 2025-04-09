package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.ApiResponse;
import com.awesomity.marketplace.marketplace_api.entity.Order;
import com.awesomity.marketplace.marketplace_api.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/orders")
@Slf4j
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        log.info("GET /api/v1/admin/orders - Fetching all orders");
        List<Order> orders = orderService.findAll();
        return ApiResponse.ok("Orders retrieved", orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long orderId) {
        log.info("GET /api/v1/admin/orders/{} - Fetching order", orderId);
        Order order = orderService.findById(orderId);
        return ApiResponse.ok("Order retrieved", order);
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(@PathVariable Long orderId,
                                                                @RequestParam String status) {
        log.info("PATCH /api/v1/admin/orders/{}/status?status={} - Updating order status", orderId, status);
        Order updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ApiResponse.ok("Order status updated", updatedOrder);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long orderId) {
        log.info("DELETE /api/v1/admin/orders/{} - Deleting order", orderId);
        orderService.deleteOrder(orderId);
        return ApiResponse.ok("Order deleted", null);
    }
}
