package com.marketer.affiliate_agent.service;

import com.marketer.affiliate_agent.dto.ContentType;
import com.marketer.affiliate_agent.dto.ScrapedProductInfo;
import com.marketer.affiliate_agent.entity.AffiliateLink;
import com.marketer.affiliate_agent.entity.GeneratedContent;
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
    private final AffiliateTransformationService affiliateTransformationService;

    public AffiliateLinkService(AffiliateLinkRepository affiliateLinkRepository,
                                LinkClickRepository linkClickRepository,
                                BitlyService bitlyService,
                                OpenAiService openAiService,
                                WebScraperService webScraperService,
                                AffiliateTransformationService affiliateTransformationService) {
        this.affiliateLinkRepository = affiliateLinkRepository;
        this.linkClickRepository = linkClickRepository;
        this.bitlyService = bitlyService;
        this.openAiService = openAiService;
        this.webScraperService = webScraperService;
        this.affiliateTransformationService = affiliateTransformationService;
    }

    public AffiliateLink createLink(String originalUrl, ContentType contentType, LocalDateTime scheduledAt) {
        // 1. Scrape product info from the original URL
        ScrapedProductInfo productInfo = webScraperService.scrapeProductInfo(originalUrl);
        String title = productInfo.getTitle();
        String description = productInfo.getDescription();
        String imageUrl = productInfo.getImageUrl();

        // 2. Transform the original URL into a monetizable affiliate link
        String affiliateUrl = affiliateTransformationService.transform(originalUrl);

        // 3. Shorten the new affiliate link
        String shortUrl = bitlyService.shortenUrl(affiliateUrl);

        // 4. Generate promotional content
        List<String> generatedContentVariations = openAiService.generatePostContent(title, description, contentType);

        // 5. Save the link and content to the database
        AffiliateLink newLink = new AffiliateLink();
        newLink.setTitle(title);
        newLink.setLongUrl(affiliateUrl); // IMPORTANT: Store the final affiliate URL
        newLink.setShortUrl(shortUrl);
        newLink.setProductImageUrl(imageUrl);
        newLink.setScheduledAt(scheduledAt);

        for (String content : generatedContentVariations) {
            GeneratedContent newContent = new GeneratedContent();
            newContent.setContent(content);
            newContent.setAffiliateLink(newLink);
            newLink.getGeneratedContent().add(newContent);
        }

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
