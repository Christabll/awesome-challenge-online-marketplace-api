package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.ApiResponse;
import com.awesomity.marketplace.marketplace_api.dto.OrderRequestDto;
import com.awesomity.marketplace.marketplace_api.entity.Order;
import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.service.OrderService;
import com.awesomity.marketplace.marketplace_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    // Place an order
    @PostMapping
    public ResponseEntity<ApiResponse> placeOrder(@Valid @RequestBody OrderRequestDto dto) {
        User currentUser = userService.getLoggedInUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("Not logged in"));
        }
        Order newOrder = orderService.placeOrder(currentUser, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order placed successfully", newOrder));
    }

    // Get order history
    @GetMapping
    public ResponseEntity<ApiResponse> getOrderHistory() {
        User currentUser = userService.getLoggedInUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("Not logged in"));
        }
        List<Order> orders = orderService.findOrdersByUser(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Order history retrieved", orders));
    }

    // Get order details
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse> getOrderById(@PathVariable Long orderId) {
        User currentUser = userService.getLoggedInUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("Not logged in"));
        }
        Order order = orderService.findOrderByIdForUser(orderId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Order details retrieved", order));
    }
}
