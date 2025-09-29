package com.marketer.affiliate_agent.scheduler;

import com.marketer.affiliate_agent.entity.AffiliateLink;
import com.marketer.affiliate_agent.entity.GeneratedContent;
import com.marketer.affiliate_agent.repository.AffiliateLinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
@ConditionalOnProperty(name = "scheduler.recurring.enabled", havingValue = "true")
public class RecurringPostScheduler {

    private static final Logger log = LoggerFactory.getLogger(RecurringPostScheduler.class);

    private final AffiliateLinkRepository affiliateLinkRepository;
    private final long repostDays;
    private final long minClicks;
    private final long staggerMinutes;
    private final int startHour;
    private final int endHour;

    public RecurringPostScheduler(AffiliateLinkRepository affiliateLinkRepository,
                                @Value("${scheduler.recurring.repost-days}") long repostDays,
                                @Value("${scheduler.recurring.min-clicks}") long minClicks,
                                @Value("${scheduler.marketing.stagger-minutes}") long staggerMinutes,
                                @Value("${scheduler.posting.window.start-hour}") int startHour,
                                @Value("${scheduler.posting.window.end-hour}") int endHour) {
        this.affiliateLinkRepository = affiliateLinkRepository;
        this.repostDays = repostDays;
        this.minClicks = minClicks;
        this.staggerMinutes = staggerMinutes;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    @Scheduled(cron = "${scheduler.recurring.cron}")
    @Transactional
    public void requeueSuccessfulPosts() {
        log.info("Checking for successful posts to re-queue...");

        LocalDateTime repostThreshold = LocalDateTime.now().minusDays(repostDays);
        List<AffiliateLink> candidates = affiliateLinkRepository
                .findAllByClickCountGreaterThanEqualAndLastPostedAtBefore(minClicks, repostThreshold);

        if (candidates.isEmpty()) {
            return;
        }

        log.info("Found {} successful links to re-queue.", candidates.size());

        for (AffiliateLink link : candidates) {
            // Find the winning content variation with the most clicks
            Optional<GeneratedContent> winningVariation = link.getGeneratedContent().stream()
                    .max(Comparator.comparing(GeneratedContent::getClickCount));

            if (winningVariation.isPresent()) {
                GeneratedContent winner = winningVariation.get();
                log.info("Re-queueing winning variation #{} for link: '{}' ({} clicks)", winner.getId(), link.getTitle(), winner.getClickCount());

                // Reset the posted status for ONLY the winning variation
                winner.setPosted(false);

                // Calculate the next available slot at the end of the queue
                LocalDateTime nextScheduleTime = affiliateLinkRepository.findTopByOrderByScheduledAtDesc()
                        .map(AffiliateLink::getScheduledAt)
                        .filter(time -> time.isAfter(LocalDateTime.now()))
                        .orElse(LocalDateTime.now());

                LocalDateTime adjustedTime = adjustForPostingWindow(nextScheduleTime.plusMinutes(staggerMinutes));

                link.setScheduledAt(adjustedTime);
            }
        }
    }

    private LocalDateTime adjustForPostingWindow(LocalDateTime time) {
        if (time.getHour() < startHour) {
            return time.withHour(startHour).withMinute(0).withSecond(0);
        } else if (time.getHour() >= endHour) {
            return time.plusDays(1).withHour(startHour).withMinute(0).withSecond(0);
        }
        return time;
    }
}
