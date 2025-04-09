package com.awesomity.marketplace.marketplace_api.dto;

import com.awesomity.marketplace.marketplace_api.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileResult {
    private User updatedUser;
    private boolean emailChanged;
}
