package com.marketer.affiliate_agent.service;

import com.marketer.affiliate_agent.entity.AffiliateLink;
import com.marketer.affiliate_agent.repository.AffiliateLinkRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PostingScheduler {

    private final AffiliateLinkRepository affiliateLinkRepository;
    private final List<SocialMediaService> socialMediaServices;

    @Value("${application.base-url}")
    private String baseUrl;

    public PostingScheduler(AffiliateLinkRepository affiliateLinkRepository, List<SocialMediaService> socialMediaServices) {
        this.affiliateLinkRepository = affiliateLinkRepository;
        this.socialMediaServices = socialMediaServices;
    }

    @Scheduled(fixedRate = 60000) // Run every 60 seconds
    public void postScheduledContent() {
        List<AffiliateLink> linksToPost = affiliateLinkRepository.findByScheduledAtBeforeAndPostedFalse(
                LocalDateTime.now());

        for (AffiliateLink link : linksToPost) {
            String trackableUrl = baseUrl + "/track/" + link.getId();
            String message = link.getGeneratedContent() + "\n" + trackableUrl;

            System.out.println("Broadcasting post for link ID: " + link.getId());

            // Broadcast the message to all social media services
            for (SocialMediaService service : socialMediaServices) {
                try {
                    service.post(message);
                } catch (Exception e) {
                    // Log the error for a specific service but continue with others
                    System.err.println("Failed to post to " + service.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }

            // Mark the link as posted after attempting to broadcast to all services
            link.setPosted(true);
            affiliateLinkRepository.save(link);
        }
    }
}
