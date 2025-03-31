package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.AuthResponse;
import com.awesomity.marketplace.marketplace_api.dto.LoginRequest;
import com.awesomity.marketplace.marketplace_api.dto.RegisterRequest;
import com.awesomity.marketplace.marketplace_api.dto.VerifyRequest;
import com.awesomity.marketplace.marketplace_api.entity.Role;
import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.entity.VerificationToken;
import com.awesomity.marketplace.marketplace_api.repository.UserRepository;
import com.awesomity.marketplace.marketplace_api.repository.VerificationTokenRepository;
import com.awesomity.marketplace.marketplace_api.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final JavaMailSender mailSender;


     // Login endpoint, Only allows login if the user's email is verified.

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isPresent() && !userOpt.get().isVerified()) {
            return ResponseEntity.badRequest().body("Please verify your email before logging in.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new AuthResponse(token, "Login successful"));
    }


     // Registration endpoint, Creates a new user with ROLE_BUYER by default and sends a verification token via email.

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already taken!");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setVerified(false);
        user.setRole(Role.ROLE_BUYER);

        userRepository.save(user);


        String token = UUID.randomUUID().toString();
        Date expiryDate = new Date(System.currentTimeMillis() + (30 * 60 * 1000));
        VerificationToken verificationToken = new VerificationToken(token, user, expiryDate);
        verificationTokenRepository.save(verificationToken);


        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Marketplace Email Verification");
        mailMessage.setText("Thank you for registering on Marketplace!\n\n" +
                "Your verification token is: " + token + "\n\n");
        mailSender.send(mailMessage);

        return ResponseEntity.ok("Registration successful! A verification token has been sent to your email. Please verify your account to enable login.");
    }


     // Email verification endpoint, User submits the token to verify their email.

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyRequest request) {
        String token = request.getToken();
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            return ResponseEntity.badRequest().body("Invalid verification token.");
        }

        if (verificationToken.getExpiryDate().before(new Date())) {
            return ResponseEntity.badRequest().body("Verification token has expired.");
        }
        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
        return ResponseEntity.ok("Email verification successful! You may now log in.");
    }

}
