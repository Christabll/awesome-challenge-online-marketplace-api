package com.awesomity.marketplace.marketplace_api.config;

import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.entity.Role;
import com.awesomity.marketplace.marketplace_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        boolean adminExists = userRepository.existsByEmail("christa.bellaishi@gmail.com");

        if (!adminExists) {
            User admin = new User();
            admin.setEmail("christa.bellaishi@gmail.com");
            admin.setFirstName("Christa");
            admin.setLastName("Admin");
            admin.setPassword(passwordEncoder.encode("Admin123!"));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setVerified(true);

            userRepository.save(admin);
            System.out.println("Default admin user created: christa.bellaishi@gmail.com / Admin123!");
        }
    }
}
