package com.marketer.affiliate_agent.dto;

import lombok.Data;

@Data
public class ScrapedProductOffer {
    private String name;
    private String url;
    private String description;
    private String imageUrl; // Optional: for richer content generation later
}
