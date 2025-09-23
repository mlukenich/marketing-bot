package com.marketer.affiliate_agent.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ResearchScheduler {

    private final ResearchService researchService;

    @Value("${research.trending.url}")
    private String trendingPageUrl;

    public ResearchScheduler(ResearchService researchService) {
        this.researchService = researchService;
    }

    // Schedule to run every hour (adjust as needed)
    @Scheduled(fixedRate = 3600000) // 3600000 ms = 1 hour
    public void performResearch() {
        System.out.println("Starting research cycle at " + LocalDateTime.now());
        try {
            researchService.researchTrendingProducts(trendingPageUrl);
            System.out.println("Research cycle completed successfully.");
        } catch (Exception e) {
            System.err.println("Error during research cycle: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
