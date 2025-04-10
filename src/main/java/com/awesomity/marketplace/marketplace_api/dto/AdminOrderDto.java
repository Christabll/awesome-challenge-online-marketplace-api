package com.awesomity.marketplace.marketplace_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderDto {
    private Long orderId;
    private String buyerName;
    private String buyerEmail;
    private String orderStatus;
    private String paymentStatus;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String paymentMethod;
    private Double amount;
    private LocalDateTime orderDate;
}
