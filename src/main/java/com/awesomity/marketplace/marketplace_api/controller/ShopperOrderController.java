package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.ApiResponse;
import com.awesomity.marketplace.marketplace_api.dto.OrderRequestDto;
import com.awesomity.marketplace.marketplace_api.entity.Order;
import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.security.CustomUserDetails;
import com.awesomity.marketplace.marketplace_api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class ShopperOrderController {

    private final OrderService orderService;


    @PostMapping
    public ResponseEntity<ApiResponse> placeOrder(@RequestBody OrderRequestDto orderRequest) {
        User currentUser = getCurrentUser();
        Order order = new Order();
        order.setUser(currentUser);
        order.setTotalAmount(orderRequest.getTotalAmount());

        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.ok(ApiResponse.success("Order placed successfully", createdOrder));
    }



    @GetMapping("/history")
    public ResponseEntity<ApiResponse> getOrderHistory() {
        User currentUser = getCurrentUser();
        List<Order> orders = orderService.findOrdersByUser(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Order history retrieved", orders));
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse> trackOrder(@PathVariable Long orderId) {
        User currentUser = getCurrentUser();
        Order order = orderService.findById(orderId);
        if (!order.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail("You are not authorized to view this order"));
        }
        return ResponseEntity.ok(ApiResponse.success("Order status retrieved", order.getStatus()));
    }


    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse> cancelOrder(@PathVariable Long orderId) {
        User currentUser = getCurrentUser();
        Order cancelledOrder = orderService.cancelOrderForShopper(orderId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", cancelledOrder));
    }


    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getUser();
    }
}
