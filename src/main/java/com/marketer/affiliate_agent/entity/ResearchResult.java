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
public class ResearchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    @Column(length = 1024)
    private String productUrl;

    @Column(length = 2048)
    private String productDescription;

    private String productImageUrl;

    @CreationTimestamp
    private LocalDateTime discoveredAt;

    private boolean processed = false;
}
