package com.awesomity.marketplace.marketplace_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDto {
    @NotBlank
    private String name;

    private String description;
}
