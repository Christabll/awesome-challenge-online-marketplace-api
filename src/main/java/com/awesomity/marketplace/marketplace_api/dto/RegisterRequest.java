package com.awesomity.marketplace.marketplace_api.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
}
