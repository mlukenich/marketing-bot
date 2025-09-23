package com.marketer.affiliate_agent.service;

import com.marketer.affiliate_agent.entity.AffiliateLink;
import com.marketer.affiliate_agent.repository.AffiliateLinkRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PostingScheduler {

    private final AffiliateLinkRepository affiliateLinkRepository;
    private final TwitterService twitterService;

    public PostingScheduler(AffiliateLinkRepository affiliateLinkRepository, TwitterService twitterService) {
        this.affiliateLinkRepository = affiliateLinkRepository;
        this.twitterService = twitterService;
    }

    @Scheduled(fixedRate = 60000) // Run every 60 seconds
    public void postScheduledTweets() {
        List<AffiliateLink> linksToPost = affiliateLinkRepository.findByScheduledAtBeforeAndPostedFalse(
                LocalDateTime.now());

        for (AffiliateLink link : linksToPost) {
            // Only post if the content type is TWEET
            // For other content types, we would need different posting mechanisms
            // For now, we assume only tweets are posted by this scheduler
            String tweetContent = link.getGeneratedContent() + "\n" + link.getShortUrl();
            twitterService.createTweet(tweetContent);

            link.setPosted(true);
            affiliateLinkRepository.save(link);
        }
    }
}
