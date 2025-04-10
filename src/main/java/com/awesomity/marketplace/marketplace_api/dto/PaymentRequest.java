package com.awesomity.marketplace.marketplace_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Schema(description = "Payment request DTO")
@Data
public class PaymentRequest {

    @NotNull
    @Schema(example = "1", description = "The ID of the order to be paid")
    private Long orderId;

    @Pattern(regexp = "\\d{4} \\d{4} \\d{4} \\d{4}", message = "Card number must be in format '1234 5678 9012 3456'")
    private String cardNumber;

    @NotBlank
    @Schema(example = "John Doe")
    private String cardHolder;

    @Pattern(regexp = "0[1-9]|1[0-2]", message = "Expiry month must be in format 'MM' (01-12)")
    @Schema(example = "08")
    private String expiryMonth;

    @Pattern(regexp = "\\d{2}", message = "Expiry year must be 2 digits (e.g., 25)")
    @Schema(example = "25")
    private String expiryYear;

    @Pattern(regexp = "\\d{3}", message = "CVV must be 3 digits")
    @Schema(example = "123")
    private String cvv;
}

