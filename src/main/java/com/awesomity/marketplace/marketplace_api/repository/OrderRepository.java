package com.awesomity.marketplace.marketplace_api.repository;

import com.awesomity.marketplace.marketplace_api.entity.Order;
import com.awesomity.marketplace.marketplace_api.entity.Payment;
import com.awesomity.marketplace.marketplace_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    @Query("SELECT CASE WHEN COUNT(oi) > 0 THEN true ELSE false END " +
            "FROM OrderItem oi JOIN oi.order o " +
            "WHERE o.user.id = :userId AND oi.product.id = :productId")
    boolean existsOrderByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);

}
