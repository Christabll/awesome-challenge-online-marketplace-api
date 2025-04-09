package com.awesomity.marketplace.marketplace_api.service.impl;

import com.awesomity.marketplace.marketplace_api.dto.UpdateProfileRequest;
import com.awesomity.marketplace.marketplace_api.dto.UpdateProfileResult;
import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.entity.VerificationToken;
import com.awesomity.marketplace.marketplace_api.exception.BadRequestException;
import com.awesomity.marketplace.marketplace_api.exception.ResourceNotFoundException;
import com.awesomity.marketplace.marketplace_api.repository.UserRepository;
import com.awesomity.marketplace.marketplace_api.repository.VerificationTokenRepository;
import com.awesomity.marketplace.marketplace_api.security.CustomUserDetails;
import com.awesomity.marketplace.marketplace_api.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails customUserDetails) {
            String email = customUserDetails.getUsername();
            return userRepository.findByEmail(email).orElse(null);
        }
        return null;
    }

    @Override
    public User create(User user) {
        User saved = userRepository.save(user);
        log.info("Created new user with ID: {}", saved.getId());
        return saved;
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public User update(User user) {
        userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + user.getId()));

        User updated = userRepository.save(user);
        log.info("Updated user with ID: {}", updated.getId());
        return updated;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void deleteById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        userRepository.delete(user);
        log.info("Deleted user with ID: {}", userId);
    }

    @Override
    @Transactional
    public UpdateProfileResult updateProfile(User currentUser, UpdateProfileRequest request) {
        boolean emailChanged = false;

        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            currentUser.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            currentUser.setLastName(request.getLastName());
        }

        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(currentUser.getEmail())) {
            userRepository.findByEmail(request.getEmail()).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(currentUser.getId())) {
                    throw new BadRequestException("Email is already taken!");
                }
            });

            currentUser.setEmail(request.getEmail());
            currentUser.setVerified(false);
            emailChanged = true;
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            currentUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updatedUser = userRepository.save(currentUser);
        log.info("Updated profile for user ID: {}", updatedUser.getId());

        if (emailChanged) {
            String token = UUID.randomUUID().toString();
            Date expiryDate = new Date(System.currentTimeMillis() + (30 * 60 * 1000));
            VerificationToken verificationToken = new VerificationToken(token, updatedUser, expiryDate);
            verificationTokenRepository.save(verificationToken);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(updatedUser.getEmail());
            mailMessage.setSubject("Marketplace Email Verification");
            mailMessage.setText("You have updated your email. Please verify it using the token below:\n\n" +
                    "Verification token: " + token + "\n\nThis token is valid for 30 minutes.");

            mailSender.send(mailMessage);
            log.info("Sent verification token to updated email: {}", updatedUser.getEmail());
        }

        return new UpdateProfileResult(updatedUser, emailChanged);
    }
}
