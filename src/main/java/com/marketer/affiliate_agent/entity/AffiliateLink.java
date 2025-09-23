package com.marketer.affiliate_agent.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class AffiliateLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String longUrl;

    private String shortUrl;

    @Column(length = 1024)
    private String generatedContent;

    @Column(length = 2048) // Allow for longer URLs
    private String productImageUrl;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime scheduledAt;

    private boolean posted = false;

    private long clickCount = 0;
}
