package com.marketer.affiliate_agent.service;

import com.marketer.affiliate_agent.entity.AffiliateLink;
import com.marketer.affiliate_agent.repository.AffiliateLinkRepository;
import org.springframework.stereotype.Service;

@Service
public class AffiliateLinkService {

    private final AffiliateLinkRepository affiliateLinkRepository;
    private final BitlyService bitlyService;

    public AffiliateLinkService(AffiliateLinkRepository affiliateLinkRepository, BitlyService bitlyService) {
        this.affiliateLinkRepository = affiliateLinkRepository;
        this.bitlyService = bitlyService;
    }

    public AffiliateLink createLink(String longUrl, String title) {
        String shortUrl = bitlyService.shortenUrl(longUrl);

        AffiliateLink newLink = new AffiliateLink();
        newLink.setTitle(title);
        newLink.setLongUrl(longUrl);
        newLink.setShortUrl(shortUrl);

        return affiliateLinkRepository.save(newLink);
    }
}
