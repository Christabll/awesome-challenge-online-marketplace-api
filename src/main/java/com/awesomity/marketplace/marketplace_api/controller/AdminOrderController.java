package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.ApiResponse;
import com.awesomity.marketplace.marketplace_api.entity.Order;
import com.awesomity.marketplace.marketplace_api.exception.ResourceNotFoundException;
import com.awesomity.marketplace.marketplace_api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;
    private static final Logger log = LoggerFactory.getLogger(AdminOrderController.class);

    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        log.info("Admin is requesting all orders");
        List<Order> orders = orderService.findAll();
        log.info("Found {} orders", orders.size());
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved", orders));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long orderId) {
        log.info("Admin requested order with id: {}", orderId);
        try {
            Order order = orderService.findById(orderId);
            log.info("Order found: {}", order);
            return ResponseEntity.ok(ApiResponse.success("Order retrieved", order));
        } catch (ResourceNotFoundException ex) {
            log.error("Order not found with id: {}", orderId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(ex.getMessage()));
        }
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(@PathVariable Long orderId,
                                                                @RequestParam String status) {
        log.info("Admin is updating order status for id: {} to status: {}", orderId, status);
        try {
            Order order = orderService.updateOrderStatus(orderId, status);
            log.info("Order updated: {}", order);
            return ResponseEntity.ok(ApiResponse.success("Order status updated", order));
        } catch (ResourceNotFoundException ex) {
            log.error("Order not found for update with id: {}", orderId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(ex.getMessage()));
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long orderId) {
        log.info("Admin is deleting order with id: {}", orderId);
        try {
            orderService.deleteOrder(orderId);
            log.info("Order deleted successfully for id: {}", orderId);
            return ResponseEntity.ok(ApiResponse.success("Order deleted", null));
        } catch (ResourceNotFoundException ex) {
            log.error("Order not found for deletion with id: {}", orderId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(ex.getMessage()));
        }
    }
}
