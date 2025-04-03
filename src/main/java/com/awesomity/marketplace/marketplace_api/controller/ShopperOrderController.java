package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.ApiResponse;
import com.awesomity.marketplace.marketplace_api.dto.OrderRequestDto;
import com.awesomity.marketplace.marketplace_api.entity.Order;
import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.exception.BadRequestException;
import com.awesomity.marketplace.marketplace_api.repository.OrderItemRepository;
import com.awesomity.marketplace.marketplace_api.security.CustomUserDetails;
import com.awesomity.marketplace.marketplace_api.service.OrderService;
import com.awesomity.marketplace.marketplace_api.service.ProductService;
import lombok.RequiredArgsConstructor;
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
    private final ProductService productService;
    private final OrderItemRepository orderItemRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<Order>> placeOrder(@RequestBody OrderRequestDto orderRequest) {
        User currentUser = getCurrentUser();
        Order order = new Order();
        order.setTotalAmount(orderRequest.getTotalAmount());

        Order createdOrder = orderService.createOrder(order, orderRequest.getItems(), currentUser);

        return ApiResponse.created("Order placed successfully", createdOrder);
    }


    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<Order>>> getOrderHistory() {
        User currentUser = getCurrentUser();
        List<Order> orders = orderService.findOrdersByUser(currentUser);
        return ApiResponse.ok("Order history retrieved", orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Order>> trackOrder(@PathVariable Long orderId) {
        User currentUser = getCurrentUser();
        Order order = orderService.findById(orderId);

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You are not authorized to view this order");
        }

        return ApiResponse.ok("Order retrieved", order);
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(@PathVariable Long orderId) {
        User currentUser = getCurrentUser();
        Order cancelledOrder = orderService.cancelOrderForShopper(orderId, currentUser);
        return ApiResponse.ok("Order cancelled successfully", cancelledOrder);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getUser();
    }
}

