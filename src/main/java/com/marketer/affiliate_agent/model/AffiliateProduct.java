package com.marketer.affiliate_agent.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class AffiliateProduct {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String description;
    private String affiliateLink;
    private String category;
    private java.time.LocalDateTime lastPromoted;
}
