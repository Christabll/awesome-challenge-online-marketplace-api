package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.ApiResponse;
import com.awesomity.marketplace.marketplace_api.dto.UpdateProfileRequest;
import com.awesomity.marketplace.marketplace_api.dto.UpdateProfileResult;
import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.exception.BadRequestException;
import com.awesomity.marketplace.marketplace_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@demo.com");
    }

    @Test
    void shouldReturnCurrentlyLoggedInUser() {
        when(userService.getLoggedInUser()).thenReturn(mockUser);

        ResponseEntity<ApiResponse<User>> response = userController.currentlyLoggedInUser();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getData().getEmail()).isEqualTo("test@demo.com");
        verify(userService).getLoggedInUser();
    }

    @Test
    void shouldThrowIfNoLoggedInUser() {
        when(userService.getLoggedInUser()).thenReturn(null);

        assertThatThrownBy(() -> userController.currentlyLoggedInUser())
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("No user is currently logged in");
    }

    @Test
    void shouldUpdateProfileSuccessfully() {
        UpdateProfileRequest request = new UpdateProfileRequest();

        UpdateProfileResult result = new UpdateProfileResult();
        result.setUpdatedUser(mockUser);
        result.setEmailChanged(false);

        when(userService.getLoggedInUser()).thenReturn(mockUser);
        when(userService.updateProfile(mockUser, request)).thenReturn(result);

        ResponseEntity<ApiResponse<User>> response = userController.updateProfile(request);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getMessage()).isEqualTo("Profile updated successfully!");
        assertThat(response.getBody().getData()).isEqualTo(mockUser);
    }

    @Test
    void shouldReturnEmailChangedMessageIfEmailChanged() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        UpdateProfileResult result = new UpdateProfileResult();
        result.setUpdatedUser(mockUser);
        result.setEmailChanged(true);

        when(userService.getLoggedInUser()).thenReturn(mockUser);
        when(userService.updateProfile(mockUser, request)).thenReturn(result);

        ResponseEntity<ApiResponse<User>> response = userController.updateProfile(request);

        assertThat(response.getBody().getMessage())
                .contains("Email changed. A verification token has been sent");
    }

    @Test
    void shouldThrowIfNotLoggedInOnProfileUpdate() {
        when(userService.getLoggedInUser()).thenReturn(null);

        assertThatThrownBy(() -> userController.updateProfile(new UpdateProfileRequest()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Not logged in");
    }
}