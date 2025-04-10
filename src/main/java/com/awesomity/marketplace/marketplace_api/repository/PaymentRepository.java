package com.awesomity.marketplace.marketplace_api.repository;

import com.awesomity.marketplace.marketplace_api.entity.Order;
import com.awesomity.marketplace.marketplace_api.entity.Payment;
import com.awesomity.marketplace.marketplace_api.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder(Order order);
    boolean existsByOrderAndStatus(Order order, PaymentStatus status);

}
