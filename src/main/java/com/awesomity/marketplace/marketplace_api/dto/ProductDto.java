package com.awesomity.marketplace.marketplace_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductDto {
    @NotBlank
    private String name;

    private String description;

    @NotNull
    private Double price;

    @NotNull
    private Integer quantity;

    @NotNull
    private String currency;

    @NotNull
    private Long categoryId;
}
