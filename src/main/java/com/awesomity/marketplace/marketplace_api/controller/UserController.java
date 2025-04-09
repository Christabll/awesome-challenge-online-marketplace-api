package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.ApiResponse;
import com.awesomity.marketplace.marketplace_api.dto.UpdateProfileRequest;
import com.awesomity.marketplace.marketplace_api.dto.UpdateProfileResult;
import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.exception.BadRequestException;
import com.awesomity.marketplace.marketplace_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/current-user")
    public ResponseEntity<ApiResponse<User>> currentlyLoggedInUser() {
        User currentUser = userService.getLoggedInUser();
        if (currentUser == null) {
            throw new BadRequestException("No user is currently logged in");
        }

        log.info("Fetched currently logged-in user: {}", currentUser.getEmail());
        return ApiResponse.ok("Currently logged-in user retrieved successfully", currentUser);
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<User>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        User currentUser = userService.getLoggedInUser();
        if (currentUser == null) {
            throw new BadRequestException("Not logged in");
        }

        UpdateProfileResult result = userService.updateProfile(currentUser, request);
        User updatedUser = result.getUpdatedUser();
        boolean emailChanged = result.isEmailChanged();

        String message = emailChanged
                ? "Email changed. A verification token has been sent to your new email address. Please verify to complete the update."
                : "Profile updated successfully!";

        log.info("User {} updated their profile", updatedUser.getId());
        return ApiResponse.ok(message, updatedUser);
    }
}
