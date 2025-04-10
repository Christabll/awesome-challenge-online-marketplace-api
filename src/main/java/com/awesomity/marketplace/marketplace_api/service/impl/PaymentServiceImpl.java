package com.awesomity.marketplace.marketplace_api.service.impl;

import com.awesomity.marketplace.marketplace_api.dto.PaymentRequest;
import com.awesomity.marketplace.marketplace_api.entity.*;
import com.awesomity.marketplace.marketplace_api.entity.Order;
import com.awesomity.marketplace.marketplace_api.exception.BadRequestException;
import com.awesomity.marketplace.marketplace_api.exception.UnAuthorizedException;
import com.awesomity.marketplace.marketplace_api.repository.PaymentRepository;
import com.awesomity.marketplace.marketplace_api.service.PaymentService;
import com.awesomity.marketplace.marketplace_api.entity.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public Payment processPayment(Order order, PaymentRequest request, User currentUser) {

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new BadRequestException("Cannot pay for a canceled order.");
        }

        // Only prevent retry if payment was SUCCESS before
        if (paymentRepository.existsByOrderAndStatus(order, PaymentStatus.SUCCESS)) {
            throw new BadRequestException("This order has already been paid.");
        }

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new UnAuthorizedException("You are not allowed to pay for someone else's order.");
        }

        // ✅ Validate card before even creating a payment
        String cleanedCardNumber = request.getCardNumber() != null ? request.getCardNumber().replaceAll(" ", "") : "";
        if (cleanedCardNumber.length() != 16) {
            throw new BadRequestException("Invalid card number provided. Card number must have 16 digits.");
        }

        try {
            Thread.sleep(1500); // Simulate delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BadRequestException("Payment processing was interrupted.");
        }

        // ✅ Create and save payment only if validation passed
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setAmount(calculateOrderTotal(order));

        return paymentRepository.save(payment);
    }


    private double calculateOrderTotal(Order order) {
        return order.getOrderItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }
}
