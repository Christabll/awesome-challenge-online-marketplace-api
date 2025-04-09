package com.awesomity.marketplace.marketplace_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "verification_tokens")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public VerificationToken(String token, User user, Date expiryDate) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }
}
