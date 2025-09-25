package com.marketer.affiliate_agent.service;

import com.marketer.affiliate_agent.dto.ContentType;
import com.marketer.affiliate_agent.dto.ScrapedProductInfo;
import com.marketer.affiliate_agent.entity.AffiliateLink;
import com.marketer.affiliate_agent.entity.LinkClick;
import com.marketer.affiliate_agent.exception.ApiException;
import com.marketer.affiliate_agent.repository.AffiliateLinkRepository;
import com.marketer.affiliate_agent.repository.LinkClickRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AffiliateLinkService {

    private final AffiliateLinkRepository affiliateLinkRepository;
    private final LinkClickRepository linkClickRepository;
    private final BitlyService bitlyService;
    private final OpenAiService openAiService;
    private final WebScraperService webScraperService;

    public AffiliateLinkService(AffiliateLinkRepository affiliateLinkRepository,
                                LinkClickRepository linkClickRepository,
                                BitlyService bitlyService,
                                OpenAiService openAiService,
                                WebScraperService webScraperService) {
        this.affiliateLinkRepository = affiliateLinkRepository;
        this.linkClickRepository = linkClickRepository;
        this.bitlyService = bitlyService;
        this.openAiService = openAiService;
        this.webScraperService = webScraperService;
    }

    public AffiliateLink createLink(String longUrl, ContentType contentType, LocalDateTime scheduledAt) {
        ScrapedProductInfo productInfo = webScraperService.scrapeProductInfo(longUrl);
        String title = productInfo.getTitle();
        String description = productInfo.getDescription();
        String imageUrl = productInfo.getImageUrl();

        String shortUrl = bitlyService.shortenUrl(longUrl);
        String generatedContent = openAiService.generatePostContent(title, description, contentType);

        AffiliateLink newLink = new AffiliateLink();
        newLink.setTitle(title);
        newLink.setLongUrl(longUrl);
        newLink.setShortUrl(shortUrl);
        newLink.setGeneratedContent(generatedContent);
        newLink.setProductImageUrl(imageUrl);
        newLink.setScheduledAt(scheduledAt);
        return affiliateLinkRepository.save(newLink);
    }

    public List<AffiliateLink> getAllLinks() {
        return affiliateLinkRepository.findAll();
    }

    public AffiliateLink getLinkById(Long id) {
        return affiliateLinkRepository.findById(id)
                .orElseThrow(() -> new ApiException("Affiliate link not found with ID: " + id));
    }

    @Transactional
    public void deleteLink(Long id) {
        if (!affiliateLinkRepository.existsById(id)) {
            throw new ApiException("Affiliate link not found with ID: " + id);
        }
        linkClickRepository.deleteByAffiliateLinkId(id);
        affiliateLinkRepository.deleteById(id);
    }

    public List<LinkClick> getLinkClicks(Long linkId) {
        if (!affiliateLinkRepository.existsById(linkId)) {
            throw new ApiException("Affiliate link not found with ID: " + linkId);
        }
        return linkClickRepository.findByAffiliateLinkId(linkId);
    }
}
