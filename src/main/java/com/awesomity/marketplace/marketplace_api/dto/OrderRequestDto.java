package com.awesomity.marketplace.marketplace_api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class OrderRequestDto {

    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItemDto> items;

    @NotNull(message = "Total amount is required")
    private double totalAmount;

    @NotEmpty(message = "Shipping address is required")
    private String shippingAddress;
}
