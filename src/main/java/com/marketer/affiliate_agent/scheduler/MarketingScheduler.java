package com.marketer.affiliate_agent.scheduler;

import com.marketer.affiliate_agent.dto.ContentType;
import com.marketer.affiliate_agent.entity.AffiliateLink;
import com.marketer.affiliate_agent.entity.ResearchResult;
import com.marketer.affiliate_agent.repository.AffiliateLinkRepository;
import com.marketer.affiliate_agent.repository.ResearchResultRepository;
import com.marketer.affiliate_agent.service.AffiliateLinkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class MarketingScheduler {

    private static final Logger log = LoggerFactory.getLogger(MarketingScheduler.class);

    private final ResearchResultRepository researchResultRepository;
    private final AffiliateLinkRepository affiliateLinkRepository;
    private final AffiliateLinkService affiliateLinkService;
    private final long staggerMinutes;

    public MarketingScheduler(ResearchResultRepository researchResultRepository,
                              AffiliateLinkRepository affiliateLinkRepository,
                              AffiliateLinkService affiliateLinkService,
                              @Value("${scheduler.marketing.stagger-minutes}") long staggerMinutes) {
        this.researchResultRepository = researchResultRepository;
        this.affiliateLinkRepository = affiliateLinkRepository;
        this.affiliateLinkService = affiliateLinkService;
        this.staggerMinutes = staggerMinutes;
    }

    @Scheduled(cron = "${scheduler.marketing.cron}")
    public void processResearchFindings() {
        log.info("Checking for unprocessed research findings...");

        Optional<ResearchResult> unprocessedResult = researchResultRepository.findFirstByProcessedFalseOrderByDiscoveredAtAsc();

        unprocessedResult.ifPresent(result -> {
            log.info("Found unprocessed research result: {}", result.getProductName());
            try {
                // Calculate the next schedule time to stagger posts
                LocalDateTime nextScheduleTime = calculateNextScheduleTime();

                affiliateLinkService.createLink(
                        result.getProductUrl(),
                        ContentType.TWEET,
                        nextScheduleTime
                );

                result.setProcessed(true);
                researchResultRepository.save(result);

                log.info("Successfully processed and scheduled post for: {} at {}", result.getProductName(), nextScheduleTime);

            } catch (Exception e) {
                log.error("Failed to process research result for: {}", result.getProductName(), e);
            }
        });
    }

    private LocalDateTime calculateNextScheduleTime() {
        // Find the latest scheduled post
        Optional<AffiliateLink> lastScheduledLink = affiliateLinkRepository.findTopByOrderByScheduledAtDesc();

        // If there's a last scheduled post and its time is in the future, schedule after it.
        // Otherwise, schedule it a few minutes from now.
        LocalDateTime baseTime = lastScheduledLink
                .map(AffiliateLink::getScheduledAt)
                .filter(time -> time.isAfter(LocalDateTime.now()))
                .orElse(LocalDateTime.now());

        return baseTime.plusMinutes(staggerMinutes);
    }
}
