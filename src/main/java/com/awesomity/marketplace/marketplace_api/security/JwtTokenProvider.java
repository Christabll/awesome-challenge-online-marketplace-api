package com.awesomity.marketplace.marketplace_api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.util.Mapper;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiresIn}")
    private int jwtExpirationInMs;

    private SecretKey key;
    private JwtParser parser;

    @PostConstruct
    public void init() {
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new IllegalStateException("JWT secret is not provided in application properties");
        }

        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.parser = Jwts.parser().verifyWith(key)
                .build();
    }


    public String generateToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        User authUser = Mapper.getUserFromDTO(userDetails);
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("userId", authUser.getId())
                .claim("user", authUser)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Jwt<?, Claims> jwt = parser.parseSignedClaims(token);
        Claims claims = jwt.getPayload();
        return (String) claims.get("sub");
    }

    public String getUserIdFromToken(String token) {
        Jwt<?, Claims> jwt = parser.parseSignedClaims(token);
        Claims claims = jwt.getPayload();
        return (String) claims.get("userId");
    }

    public boolean validateToken(String authToken) {
        try {
            parser.parseSignedClaims(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature", ex);
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token", ex);
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token", ex);
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token", ex);
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty", ex);
        }
        return false;
    }
}