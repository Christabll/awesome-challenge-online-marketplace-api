package com.awesomity.marketplace.marketplace_api.service;

import com.awesomity.marketplace.marketplace_api.dto.UpdateProfileRequest;
import com.awesomity.marketplace.marketplace_api.dto.UpdateProfileResult;
import com.awesomity.marketplace.marketplace_api.entity.User;

import java.util.List;

public interface UserService {
    User getLoggedInUser();
    User create(User user);
    User findById(Long id);
    User update(User user);
    List<User> findAll();
    void deleteById(Long userId);
    UpdateProfileResult updateProfile(User currentUser, UpdateProfileRequest request);
}
