package com.awesomity.marketplace.marketplace_api.dto;


import com.awesomity.marketplace.marketplace_api.entity.User;

public class UpdateProfileResult {
    private User updatedUser;
    private boolean emailChanged;

    public UpdateProfileResult(User updatedUser, boolean emailChanged) {
        this.updatedUser = updatedUser;
        this.emailChanged = emailChanged;
    }

    public User getUpdatedUser() {
        return updatedUser;
    }

    public boolean isEmailChanged() {
        return emailChanged;
    }
}
