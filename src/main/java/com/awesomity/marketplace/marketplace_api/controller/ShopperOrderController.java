package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.ApiResponse;
import com.awesomity.marketplace.marketplace_api.dto.OrderItemDto;
import com.awesomity.marketplace.marketplace_api.dto.OrderRequestDto;
import com.awesomity.marketplace.marketplace_api.entity.Order;
import com.awesomity.marketplace.marketplace_api.entity.OrderItem;
import com.awesomity.marketplace.marketplace_api.entity.Product;
import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.exception.BadRequestException;
import com.awesomity.marketplace.marketplace_api.exception.ResourceNotFoundException;
import com.awesomity.marketplace.marketplace_api.repository.OrderItemRepository;
import com.awesomity.marketplace.marketplace_api.security.CustomUserDetails;
import com.awesomity.marketplace.marketplace_api.service.OrderService;
import com.awesomity.marketplace.marketplace_api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final ProductService productService;
    private final OrderItemRepository orderItemRepository;
    private static final Logger log = LoggerFactory.getLogger(ShopperOrderController.class);

    @PostMapping
    public ResponseEntity<ApiResponse> placeOrder(@RequestBody OrderRequestDto orderRequest) {
        try {
            User currentUser = getCurrentUser();
            Order order = new Order();
            order.setUser(currentUser);
            order.setTotalAmount(orderRequest.getTotalAmount());

            Order createdOrder = orderService.createOrder(order);
            log.info("Order placed successfully with id: {}", createdOrder.getId());

            // Process each order item
            for (OrderItemDto orderItemDto : orderRequest.getItems()) {
                Product product = productService.findById(orderItemDto.getProductId());
                OrderItem orderItem = new OrderItem(orderItemDto, createdOrder, product);
                orderItemRepository.save(orderItem);
            }
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Order placed successfully", createdOrder));
        } catch (ResourceNotFoundException | BadRequestException ex) {
            log.error("Error placing order: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ex.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse> getOrderHistory() {
        try {
            User currentUser = getCurrentUser();
            List<Order> orders = orderService.findOrdersByUser(currentUser);
            log.info("Order history retrieved for user id: {}", currentUser.getId());
            return ResponseEntity.ok(ApiResponse.success("Order history retrieved", orders));
        } catch (ResourceNotFoundException ex) {
            log.error("Order history not found: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(ex.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<?>> trackOrder(@PathVariable Long orderId) {
        try {
            Order order = orderService.findById(orderId);
            User currentUser = getCurrentUser();
            if (!order.getUser().getId().equals(currentUser.getId())) {
                log.warn("User {} is not authorized to view order {}", currentUser.getId(), orderId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.fail("You are not authorized to view this order"));
            }
            log.info("Order {} retrieved for user {}", orderId, currentUser.getId());
            return ResponseEntity.ok(ApiResponse.success("Order retrieved", order));
        } catch (ResourceNotFoundException ex) {
            log.error("Order {} not found", orderId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(ex.getMessage()));
        }
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse> cancelOrder(@PathVariable Long orderId) {
        try {
            User currentUser = getCurrentUser();
            Order cancelledOrder = orderService.cancelOrderForShopper(orderId, currentUser);
            log.info("Order {} cancelled for user {}", orderId, currentUser.getId());
            return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", cancelledOrder));
        } catch (ResourceNotFoundException ex) {
            log.error("Order {} not found for cancellation", orderId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(ex.getMessage()));
        } catch (BadRequestException ex) {
            log.error("Bad request for cancelling order {}: {}", orderId, ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ex.getMessage()));
        }
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getUser();
    }
}
