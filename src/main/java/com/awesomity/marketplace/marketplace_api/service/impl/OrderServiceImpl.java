package com.awesomity.marketplace.marketplace_api.service.impl;

import com.awesomity.marketplace.marketplace_api.dto.OrderRequestDto;
import com.awesomity.marketplace.marketplace_api.entity.Order;
import com.awesomity.marketplace.marketplace_api.entity.OrderStatus;
import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.exception.ResourceNotFoundException;
import com.awesomity.marketplace.marketplace_api.repository.OrderRepository;
import com.awesomity.marketplace.marketplace_api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }


    @Override
    public Order updateStatus(Long orderId, String status) {
        Order order = findById(orderId);
        try {
            order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }
        return orderRepository.save(order);
    }

    @Override
    public Order placeOrder(User user, OrderRequestDto dto) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PLACED);

        return orderRepository.save(order);
    }


    @Override
    public List<Order> findOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }


    @Override
    public Order findOrderByIdForUser(Long orderId, User user) {
        Order order = findById(orderId);
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Order", "id", orderId);
        }
        return order;
    }
}
