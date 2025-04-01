package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.ApiResponse;
import com.awesomity.marketplace.marketplace_api.dto.RegisterRequest;
import com.awesomity.marketplace.marketplace_api.entity.Role;
import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved", users));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        userService.deleteById(userId);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
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
        return ResponseEntity.ok(ApiResponse.success("Admin registered successfully", createdUser));
    }

    @PatchMapping("/users/{userId}/promote-to-seller")
    public ResponseEntity<ApiResponse<User>> promoteToSeller(@PathVariable Long userId) {
        User user = userService.findById(userId);
        user.setRole(Role.ROLE_SELLER);
        User updated = userService.update(user);
        return ResponseEntity.ok(ApiResponse.success("User promoted to seller successfully", updated));
    }
}
