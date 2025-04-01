package com.awesomity.marketplace.marketplace_api.repository;

import com.awesomity.marketplace.marketplace_api.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
