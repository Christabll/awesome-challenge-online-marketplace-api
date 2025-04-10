package com.awesomity.marketplace.marketplace_api.service;

import com.awesomity.marketplace.marketplace_api.dto.AdminOrderDto;
import com.awesomity.marketplace.marketplace_api.dto.OrderItemDto;
import com.awesomity.marketplace.marketplace_api.entity.Order;
import com.awesomity.marketplace.marketplace_api.entity.User;
import java.util.List;

public interface OrderService {

    Order createOrder(Order order, List<OrderItemDto> orderItems, User user);
    Order updateOrderStatus(Long orderId, String newStatus);
    Order findById(Long orderId);
    List<AdminOrderDto> findAll();
    void deleteOrder(Long orderId);
    List<Order> findOrdersByUser(User user);
    Order cancelOrderForShopper(Long orderId, User user);
    List<AdminOrderDto> getAllOrdersWithPayments();



}
