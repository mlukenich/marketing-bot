package com.marketer.affiliate_agent.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class LinkClick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliate_link_id", nullable = false)
    private AffiliateLink affiliateLink;

    @CreationTimestamp
    private LocalDateTime clickedAt;

    // Potentially add more fields for analytics later, e.g.,
    // private String ipAddress;
    // private String userAgent;
    // private String referrer;
}
