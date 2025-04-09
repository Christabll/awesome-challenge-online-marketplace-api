package com.awesomity.marketplace.marketplace_api.serviceImpl;


import com.awesomity.marketplace.marketplace_api.dto.UpdateProfileRequest;
import com.awesomity.marketplace.marketplace_api.dto.UpdateProfileResult;
import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.exception.BadRequestException;
import com.awesomity.marketplace.marketplace_api.exception.ResourceNotFoundException;
import com.awesomity.marketplace.marketplace_api.repository.UserRepository;
import com.awesomity.marketplace.marketplace_api.repository.VerificationTokenRepository;
import com.awesomity.marketplace.marketplace_api.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private VerificationTokenRepository verificationTokenRepository;
    @Mock private JavaMailSender mailSender;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldUpdateProfileWithEmailChange() {
        User user = new User();
        user.setId(1L);
        user.setEmail("old@example.com");

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setEmail("new@example.com");
        request.setFirstName("New");
        request.setLastName("User");
        request.setPassword("newpass");

        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(passwordEncoder.encode("newpass")).thenReturn("hashed");

        UpdateProfileResult result = userService.updateProfile(user, request);

        assertThat(result).isNotNull();
        assertThat(result.getUpdatedUser().getEmail()).isEqualTo("new@example.com");
        assertThat(result.isEmailChanged()).isTrue();
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldThrowIfEmailIsTaken() {
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail("user@example.com");

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setEmail("taken@example.com");

        User existing = new User();
        existing.setId(2L);
        when(userRepository.findByEmail("taken@example.com")).thenReturn(Optional.of(existing));

        assertThrows(BadRequestException.class, () -> userService.updateProfile(currentUser, request));
    }

    @Test
    void shouldUpdateProfileWithoutEmailChange() {
        User user = new User();
        user.setId(1L);
        user.setEmail("same@example.com");

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setEmail("same@example.com");
        request.setFirstName("Same");
        request.setLastName("Name");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateProfileResult result = userService.updateProfile(user, request);

        assertThat(result).isNotNull();
        assertThat(result.getUpdatedUser().getFirstName()).isEqualTo("Same");
        assertThat(result.isEmailChanged()).isFalse();
        verify(mailSender, never()).send(any(SimpleMailMessage.class));

    }

    @Test
    void shouldDeleteUserById() {
        User user = new User(); user.setId(3L);
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteById(3L);

        verify(userRepository).delete(user);
    }

    @Test
    void shouldThrowIfUserToDeleteNotFound() {
        when(userRepository.findById(404L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteById(404L));
    }
}