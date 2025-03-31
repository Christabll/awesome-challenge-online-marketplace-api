package com.awesomity.marketplace.marketplace_api.service;

import com.awesomity.marketplace.marketplace_api.dto.OrderRequestDto;
import com.awesomity.marketplace.marketplace_api.entity.Order;
import com.awesomity.marketplace.marketplace_api.entity.User;

import java.util.List;

public interface OrderService {
    List<Order> findAll();
    Order updateStatus(Long orderId, String status);
    Order findById(Long orderId);
    Order placeOrder(User user, OrderRequestDto dto);
    List<Order> findOrdersByUser(User user);
    Order findOrderByIdForUser(Long orderId, User user);
}
