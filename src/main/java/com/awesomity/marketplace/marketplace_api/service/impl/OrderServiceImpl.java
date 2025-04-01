package com.awesomity.marketplace.marketplace_api.service.impl;

import com.awesomity.marketplace.marketplace_api.entity.Order;
import com.awesomity.marketplace.marketplace_api.entity.OrderStatus;
import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.exception.ResourceNotFoundException;
import com.awesomity.marketplace.marketplace_api.exception.BadRequestException;
import com.awesomity.marketplace.marketplace_api.repository.OrderRepository;
import com.awesomity.marketplace.marketplace_api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final JavaMailSender mailSender;


    @Override
    public Order createOrder(Order order) {
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PLACED);
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        OrderStatus statusEnum = OrderStatus.valueOf(newStatus.toUpperCase());
        order.setStatus(statusEnum);
        Order updatedOrder = orderRepository.save(order);

        sendStatusUpdateEmail(updatedOrder);
        return updatedOrder;
    }

    private void sendStatusUpdateEmail(Order order) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(order.getUser().getEmail());
        message.setSubject("Your Order Status Has Been Updated");
        message.setText("Hello " + order.getUser().getFirstName() + ",\n\n" +
                "Your order with ID " + order.getId() + " has been updated to: " + order.getStatus() + ".\n\n" +
                "Thank you for shopping with us!\n" +
                "Marketplace Team");
        mailSender.send(message);
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
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order", "id", orderId);
        }
        orderRepository.deleteById(orderId);
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
            throw new BadRequestException("You are not authorized to cancel this order.");
        }
        if (!(order.getStatus() == OrderStatus.PLACED || order.getStatus() == OrderStatus.PROCESSING)) {
            throw new BadRequestException("Order cannot be cancelled at this stage.");
        }
        order.setStatus(OrderStatus.CANCELED);
        return orderRepository.save(order);
    }
}
