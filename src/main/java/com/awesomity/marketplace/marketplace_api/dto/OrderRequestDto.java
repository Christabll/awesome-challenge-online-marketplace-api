package com.awesomity.marketplace.marketplace_api.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class OrderRequestDto {

    @NotEmpty
    private List<Long> productIds; // or a list of order item details if needed
    // You can add additional fields like shipping address, payment info, etc.

}
