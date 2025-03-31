package com.awesomity.marketplace.marketplace_api.service.impl;

import com.awesomity.marketplace.marketplace_api.dto.UpdateProfileRequest;
import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.entity.VerificationToken;
import com.awesomity.marketplace.marketplace_api.exception.ResourceNotFoundException;
import com.awesomity.marketplace.marketplace_api.repository.UserRepository;
import com.awesomity.marketplace.marketplace_api.repository.VerificationTokenRepository;
import com.awesomity.marketplace.marketplace_api.security.CustomUserDetails;
import com.awesomity.marketplace.marketplace_api.service.UserService;
import org.springframework.mail.javamail.JavaMailSender;
import com.awesomity.marketplace.marketplace_api.dto.UpdateProfileResult;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;



    @Override
    public User getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            String email = ((CustomUserDetails) principal).getUsername();
            Optional<User> userOptional = userRepository.findByEmail(email);
            return userOptional.orElse(null);
        }
        return null;
    }

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void deleteById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        userRepository.deleteById(userId);
    }

//Update user profile
    @Override
    public UpdateProfileResult updateProfile(User currentUser, UpdateProfileRequest request) {
        boolean emailChanged = false;

        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            currentUser.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            currentUser.setLastName(request.getLastName());
        }

        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(currentUser.getEmail())) {
            Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(currentUser.getId())) {

                throw new IllegalArgumentException("Email is already taken!");
            }
            currentUser.setEmail(request.getEmail());
            currentUser.setVerified(false);
            emailChanged = true;
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            currentUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updatedUser = userRepository.save(currentUser);

        // If the email has changed, generate a new verification token and send email
        if (emailChanged) {
            String token = UUID.randomUUID().toString();
            Date expiryDate = new Date(System.currentTimeMillis() + (30 * 60 * 1000));
            VerificationToken verificationToken = new VerificationToken(token, updatedUser, expiryDate);
            verificationTokenRepository.save(verificationToken);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(updatedUser.getEmail());
            mailMessage.setSubject("Marketplace Email Verification");
            mailMessage.setText("You have updated your email. Please verify your new email address using the token below:\n\n" +
                    "Your verification token is: " + token + "\n\n" +
                    "This token is valid for 30 minutes.");
            mailSender.send(mailMessage);
        }

        return new UpdateProfileResult(updatedUser, emailChanged);
    }

}
