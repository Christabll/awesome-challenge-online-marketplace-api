package com.awesomity.marketplace.marketplace_api.service.impl;

import com.awesomity.marketplace.marketplace_api.dto.OrderItemDto;
import com.awesomity.marketplace.marketplace_api.entity.*;
import com.awesomity.marketplace.marketplace_api.exception.BadRequestException;
import com.awesomity.marketplace.marketplace_api.exception.ResourceNotFoundException;
import com.awesomity.marketplace.marketplace_api.repository.OrderItemRepository;
import com.awesomity.marketplace.marketplace_api.repository.OrderRepository;
import com.awesomity.marketplace.marketplace_api.service.OrderService;
import com.awesomity.marketplace.marketplace_api.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;
    private final JavaMailSender mailSender;


    @Override
    public Order createOrder(Order order, List<OrderItemDto> orderItems, User user) {
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PLACED);

        Order savedOrder = orderRepository.save(order);

        for (OrderItemDto itemDto : orderItems) {
            Product product = productService.findById(itemDto.getProductId());
            OrderItem item = new OrderItem(itemDto, savedOrder, product);
            orderItemRepository.save(item);
        }

        return savedOrder;
    }


    @Override
    public Order updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        OrderStatus statusEnum = OrderStatus.valueOf(newStatus.toUpperCase());
        order.setStatus(statusEnum);

        Order updatedOrder = orderRepository.save(order);
        log.info("Updated order status. Order ID: {}, New Status: {}", updatedOrder.getId(), updatedOrder.getStatus());

        sendStatusUpdateEmail(updatedOrder);
        return updatedOrder;
    }


    private void sendStatusUpdateEmail(Order order) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(order.getUser().getEmail());
        message.setSubject("Your Order Status Has Been Updated");
        message.setText("Hello " + order.getUser().getFirstName() + ",\n\n" +
                "Your order with ID " + order.getId() + " has been updated to: " + order.getStatus() + ".\n\n" +
                "Thank you for shopping with us!\nMarketplace Team");

        mailSender.send(message);
        log.info("Sent status update email for order ID: {}", order.getId());
    }

    @Override
    public Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }


    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }


    @Override
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        // Avoid ConcurrentModificationException by copying to a new list
        List<OrderItem> itemsToDelete = new ArrayList<>(order.getOrderItems());

        orderItemRepository.deleteAll(itemsToDelete);
        orderRepository.delete(order);

        log.info("Deleted order with ID: {}", orderId);
    }



    @Override
    public List<Order> findOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    @Override
    public Order cancelOrderForShopper(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.getUser().getId().equals(user.getId())) {
            log.warn("User {} attempted to cancel order {} that doesn't belong to them", user.getId(), orderId);
            throw new BadRequestException("You are not authorized to cancel this order.");
        }

        if (!(order.getStatus() == OrderStatus.PLACED || order.getStatus() == OrderStatus.PROCESSING)) {
            log.warn("Order {} cannot be cancelled in its current status: {}", orderId, order.getStatus());
            throw new BadRequestException("Order cannot be cancelled at this stage.");
        }

        order.setStatus(OrderStatus.CANCELED);

        Order cancelled = orderRepository.save(order);

        log.info("Order {} cancelled successfully by user {}", cancelled.getId(), user.getId());
        return cancelled;
    }


}
