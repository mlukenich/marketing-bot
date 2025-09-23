package com.marketer.affiliate_agent.scheduler;

import com.marketer.affiliate_agent.dto.ContentType;
import com.marketer.affiliate_agent.entity.ResearchResult;
import com.marketer.affiliate_agent.repository.ResearchResultRepository;
import com.marketer.affiliate_agent.service.AffiliateLinkService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class MarketingScheduler {

    private final ResearchResultRepository researchResultRepository;
    private final AffiliateLinkService affiliateLinkService;

    public MarketingScheduler(ResearchResultRepository researchResultRepository, AffiliateLinkService affiliateLinkService) {
        this.researchResultRepository = researchResultRepository;
        this.affiliateLinkService = affiliateLinkService;
    }

    // Run every 5 minutes to check for unprocessed research
    @Scheduled(fixedRate = 300000)
    public void processResearchFindings() {
        System.out.println("Checking for unprocessed research findings at " + LocalDateTime.now());

        // Find the oldest unprocessed research result
        Optional<ResearchResult> unprocessedResult = researchResultRepository.findFirstByProcessedFalseOrderByDiscoveredAtAsc();

        unprocessedResult.ifPresent(result -> {
            System.out.println("Found unprocessed research result: " + result.getProductName());
            try {
                // Trigger the marketing pipeline
                affiliateLinkService.createLink(
                        result.getProductUrl(),
                        ContentType.TWEET, // Default to creating a tweet
                        LocalDateTime.now().plusMinutes(15) // Schedule it for 15 mins in the future
                );

                // Mark the result as processed
                result.setProcessed(true);
                researchResultRepository.save(result);

                System.out.println("Successfully processed and scheduled post for: " + result.getProductName());

            } catch (Exception e) {
                System.err.println("Failed to process research result for: " + result.getProductName());
                e.printStackTrace();
                // Optionally, you could add logic here to mark the result as failed
                // to avoid retrying it indefinitely.
            }
        });
    }
}
