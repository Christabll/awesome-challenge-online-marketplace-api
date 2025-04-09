package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.ApiResponse;
import com.awesomity.marketplace.marketplace_api.dto.RegisterRequest;
import com.awesomity.marketplace.marketplace_api.entity.Role;
import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AdminUserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminUserController adminUserController;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setFirstName("Christa");
        user.setLastName("Bella");
        user.setEmail("christa@example.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.ROLE_BUYER);
        user.setVerified(true);
    }

    @Test
    void shouldGetAllUsers() {
        when(userService.findAll()).thenReturn(List.of(user));

        ResponseEntity<ApiResponse<List<User>>> response = adminUserController.getAllUsers();

        assertThat(response.getBody().getData()).hasSize(1);
        verify(userService).findAll();
    }

    @Test
    void shouldDeleteUserById() {
        doNothing().when(userService).deleteById(1L);

        ResponseEntity<ApiResponse<Void>> response = adminUserController.deleteUser(1L);

        assertThat(response.getBody().getMessage()).isEqualTo("User deleted successfully");
        verify(userService).deleteById(1L);
    }

    @Test
    void shouldRegisterAdminSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("admin@example.com");
        request.setFirstName("Admin");
        request.setLastName("User");
        request.setPassword("123456");

        User created = new User();
        created.setEmail(request.getEmail());
        created.setFirstName(request.getFirstName());
        created.setLastName(request.getLastName());
        created.setPassword("encodedPassword");
        created.setRole(Role.ROLE_ADMIN);
        created.setVerified(true);

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userService.create(any(User.class))).thenReturn(created);

        ResponseEntity<ApiResponse<User>> response = adminUserController.registerAdmin(request);

        assertThat(response.getBody().getMessage()).isEqualTo("Admin registered successfully");
        assertThat(response.getBody().getData().getEmail()).isEqualTo("admin@example.com");
    }

    @Test
    void shouldPromoteUserToSeller() {
        user.setRole(Role.ROLE_BUYER);
        when(userService.findById(1L)).thenReturn(user);
        when(userService.update(any(User.class))).thenReturn(user);

        ResponseEntity<ApiResponse<User>> response = adminUserController.promoteToSeller(1L);

        assertThat(response.getBody().getMessage()).isEqualTo("User promoted to seller successfully");
        assertThat(response.getBody().getData().getRole()).isEqualTo(Role.ROLE_SELLER);
    }
}
