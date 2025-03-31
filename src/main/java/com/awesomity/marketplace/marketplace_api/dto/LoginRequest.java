package com.awesomity.marketplace.marketplace_api.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
