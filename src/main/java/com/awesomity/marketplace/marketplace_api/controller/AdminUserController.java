package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.ApiResponse;
import com.awesomity.marketplace.marketplace_api.dto.RegisterRequest;
import com.awesomity.marketplace.marketplace_api.entity.Role;
import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminUserController {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.findAll();
        log.info("Admin fetched {} users", users.size());
        return ApiResponse.ok("Users retrieved", users);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        userService.deleteById(userId);
        log.info("Admin deleted user with ID: {}", userId);
        return ApiResponse.ok("User deleted successfully", null);
    }

    @PostMapping("/register-admin")
    public ResponseEntity<ApiResponse<User>> registerAdmin(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_ADMIN);
        user.setVerified(true);

        User createdUser = userService.create(user);
        log.info("New admin registered with email: {}", createdUser.getEmail());
        return ApiResponse.ok("Admin registered successfully", createdUser);
    }

    @PatchMapping("/users/{userId}/promote-to-seller")
    public ResponseEntity<ApiResponse<User>> promoteToSeller(@PathVariable Long userId) {
        User user = userService.findById(userId);
        user.setRole(Role.ROLE_SELLER);
        User updated = userService.update(user);
        log.info("User ID {} promoted to ROLE_SELLER", updated.getId());
        return ApiResponse.ok("User promoted to seller successfully", updated);
    }
}
