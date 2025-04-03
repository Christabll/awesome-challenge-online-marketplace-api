package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.*;
import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.entity.VerificationToken;
import com.awesomity.marketplace.marketplace_api.exception.BadRequestException;
import com.awesomity.marketplace.marketplace_api.repository.UserRepository;
import com.awesomity.marketplace.marketplace_api.repository.VerificationTokenRepository;
import com.awesomity.marketplace.marketplace_api.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import java.util.Date;
import java.util.Optional;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserRepository userRepository;
    @Mock private VerificationTokenRepository verificationTokenRepository;
    @Mock private JwtTokenProvider tokenProvider;
    @Mock private JavaMailSender mailSender;

    @Mock private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldFailLoginWhenUserNotVerified() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("password");

        User user = new User();
        user.setEmail("user@example.com");
        user.setVerified(false);

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));

        org.junit.jupiter.api.Assertions.assertThrows(BadRequestException.class, () -> {
            authController.login(loginRequest);
        });

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void shouldFailRegisterWhenEmailAlreadyTaken() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("john@example.com");
        registerRequest.setPassword("password");

        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        org.junit.jupiter.api.Assertions.assertThrows(BadRequestException.class, () -> {
            authController.register(registerRequest);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldFailVerifyUserWhenTokenIsExpired() {
        User user = new User();
        user.setEmail("john@example.com");

        Date expiredTime = new Date(System.currentTimeMillis() - (30 * 60 * 1000));
        VerificationToken expiredToken = new VerificationToken("token123", user, expiredTime);

        when(verificationTokenRepository.findByToken("token123")).thenReturn(expiredToken);

        VerifyRequest request = new VerifyRequest();
        request.setToken("token123");

        org.junit.jupiter.api.Assertions.assertThrows(BadRequestException.class, () -> {
            authController.verifyUser(request);
        });

        verify(userRepository, never()).save(any());
        verify(verificationTokenRepository, never()).delete(any());
    }

}
