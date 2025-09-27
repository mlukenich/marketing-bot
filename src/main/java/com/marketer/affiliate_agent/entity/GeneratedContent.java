package com.marketer.affiliate_agent.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class GeneratedContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(length = 2048)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliate_link_id")
    private AffiliateLink affiliateLink;

    private long clickCount = 0;

    private boolean posted = false;
}
