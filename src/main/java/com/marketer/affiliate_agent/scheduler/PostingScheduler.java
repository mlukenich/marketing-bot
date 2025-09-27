package com.marketer.affiliate_agent.scheduler;

import com.marketer.affiliate_agent.entity.AffiliateLink;
import com.marketer.affiliate_agent.entity.GeneratedContent;
import com.marketer.affiliate_agent.repository.GeneratedContentRepository;
import com.marketer.affiliate_agent.service.SocialMediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class PostingScheduler {

    private static final Logger log = LoggerFactory.getLogger(PostingScheduler.class);

    private final GeneratedContentRepository generatedContentRepository;
    private final List<SocialMediaService> socialMediaServices;

    @Value("${application.base-url}")
    private String baseUrl;

    public PostingScheduler(GeneratedContentRepository generatedContentRepository, List<SocialMediaService> socialMediaServices) {
        this.generatedContentRepository = generatedContentRepository;
        this.socialMediaServices = socialMediaServices;
    }

    @Scheduled(cron = "${scheduler.posting.cron}")
    public void postScheduledContent() {
        // Find the next single piece of content that is due to be posted
        Optional<GeneratedContent> contentToPostOpt = generatedContentRepository
                .findFirstByPostedFalseAndAffiliateLinkScheduledAtBeforeOrderByAffiliateLinkScheduledAtAsc(LocalDateTime.now());

        if (contentToPostOpt.isEmpty()) {
            return; // Nothing to post
        }

        GeneratedContent contentToPost = contentToPostOpt.get();
        AffiliateLink parentLink = contentToPost.getAffiliateLink();
        String trackableUrl = baseUrl + "/track/" + contentToPost.getId();

        log.info("Found content variation #{} for link '{}' to post.", contentToPost.getId(), parentLink.getTitle());

        // Broadcast the post to all social media services
        for (SocialMediaService service : socialMediaServices) {
            try {
                // We still pass the parent link so services can access image, etc.
                service.post(parentLink, trackableUrl);
            } catch (Exception e) {
                log.error("Failed to post to {}: {}", service.getClass().getSimpleName(), e.getMessage(), e);
            }
        }

        // Mark this specific content variation as posted
        contentToPost.setPosted(true);
        generatedContentRepository.save(contentToPost);
        log.info("Successfully broadcasted and marked content #{} as posted.", contentToPost.getId());
    }
}
