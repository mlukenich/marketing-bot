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
    private final int startHour;
    private final int endHour;

    public MarketingScheduler(ResearchResultRepository researchResultRepository,
                              AffiliateLinkRepository affiliateLinkRepository,
                              AffiliateLinkService affiliateLinkService,
                              @Value("${scheduler.marketing.stagger-minutes}") long staggerMinutes,
                              @Value("${scheduler.posting.window.start-hour}") int startHour,
                              @Value("${scheduler.posting.window.end-hour}") int endHour) {
        this.researchResultRepository = researchResultRepository;
        this.affiliateLinkRepository = affiliateLinkRepository;
        this.affiliateLinkService = affiliateLinkService;
        this.staggerMinutes = staggerMinutes;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    @Scheduled(cron = "${scheduler.marketing.cron}")
    public void processResearchFindings() {
        log.info("Checking for unprocessed research findings...");

        Optional<ResearchResult> unprocessedResult = researchResultRepository.findFirstByProcessedFalseOrderByDiscoveredAtAsc();

        unprocessedResult.ifPresent(result -> {
            log.info("Found unprocessed research result: {}", result.getProductName());
            try {
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
        Optional<AffiliateLink> lastScheduledLink = affiliateLinkRepository.findTopByOrderByScheduledAtDesc();

        LocalDateTime baseTime = lastScheduledLink
                .map(AffiliateLink::getScheduledAt)
                .filter(time -> time.isAfter(LocalDateTime.now()))
                .orElse(LocalDateTime.now());

        LocalDateTime nextPotentialTime = baseTime.plusMinutes(staggerMinutes);

        // Adjust for the posting window
        if (nextPotentialTime.getHour() < startHour) {
            // If it's too early, schedule it for the start of the window on the same day
            return nextPotentialTime.withHour(startHour).withMinute(0).withSecond(0);
        } else if (nextPotentialTime.getHour() >= endHour) {
            // If it's too late, schedule it for the start of the window on the next day
            return nextPotentialTime.plusDays(1).withHour(startHour).withMinute(0).withSecond(0);
        }

        return nextPotentialTime;
    }
}
