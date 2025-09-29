package com.marketer.affiliate_agent.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class AffiliateLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String longUrl;

    private String shortUrl;

    @OneToMany(mappedBy = "affiliateLink", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GeneratedContent> generatedContent = new ArrayList<>();

    @Column(length = 2048)
    private String productImageUrl;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime scheduledAt;

    private LocalDateTime lastPostedAt;

    private long clickCount = 0;
}
