package com.awesomity.marketplace.marketplace_api.repository;

import com.awesomity.marketplace.marketplace_api.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
}
