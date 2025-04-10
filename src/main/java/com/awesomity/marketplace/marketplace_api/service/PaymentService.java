package com.awesomity.marketplace.marketplace_api.service;

import com.awesomity.marketplace.marketplace_api.dto.PaymentRequest;
import com.awesomity.marketplace.marketplace_api.entity.Order;
import com.awesomity.marketplace.marketplace_api.entity.Payment;
import com.awesomity.marketplace.marketplace_api.entity.User;

public interface PaymentService {
    Payment processPayment(Order order, PaymentRequest request, User currentUser);

}
