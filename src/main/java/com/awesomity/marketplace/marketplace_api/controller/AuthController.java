package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.*;
import com.awesomity.marketplace.marketplace_api.entity.Role;
import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.entity.VerificationToken;
import com.awesomity.marketplace.marketplace_api.exception.BadRequestException;
import com.awesomity.marketplace.marketplace_api.repository.UserRepository;
import com.awesomity.marketplace.marketplace_api.repository.VerificationTokenRepository;
import com.awesomity.marketplace.marketplace_api.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;


@Slf4j
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

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isPresent() && !userOpt.get().isVerified()) {
            throw new BadRequestException("Please verify your email before logging in.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);
        log.info("User logged in: {}", request.getEmail());

        return ApiResponse.ok("Login successful", new AuthResponse(token, "Login successful"));
    }


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already taken!");
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
        mailMessage.setText("Thank you for registering on Marketplace!\n\nYour verification token is: " + token);
        mailSender.send(mailMessage);

        log.info("Verification email sent to {}", user.getEmail());

        return ApiResponse.ok(
                "Registration successful! A verification token has been sent to your email. Please verify your account to enable login.",
                null
        );
    }


    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyUser(@RequestBody VerifyRequest request) {
        String token = request.getToken();
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        if (verificationToken == null) {
            throw new BadRequestException("Invalid verification token.");
        }

        if (verificationToken.getExpiryDate().before(new Date())) {
            throw new BadRequestException("Verification token has expired.");
        }

        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);

        log.info("User verified: {}", user.getEmail());

        return ApiResponse.ok("Email verification successful! You may now log in.", null);
    }
}
