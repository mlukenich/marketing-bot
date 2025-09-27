package com.marketer.affiliate_agent.service;

import com.marketer.affiliate_agent.dto.ContentType;
import com.marketer.affiliate_agent.dto.ScrapedProductInfo;
import com.marketer.affiliate_agent.entity.AffiliateLink;
import com.marketer.affiliate_agent.repository.AffiliateLinkRepository;
import com.marketer.affiliate_agent.repository.LinkClickRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AffiliateLinkServiceTest {

    @Mock
    private AffiliateLinkRepository affiliateLinkRepository;
    @Mock
    private LinkClickRepository linkClickRepository;
    @Mock
    private BitlyService bitlyService;
    @Mock
    private OpenAiService openAiService;
    @Mock
    private WebScraperService webScraperService;
    @Mock
    private AffiliateTransformationService affiliateTransformationService;

    @InjectMocks
    private AffiliateLinkService affiliateLinkService;

    private ScrapedProductInfo scrapedInfo;

    @BeforeEach
    void setUp() {
        scrapedInfo = new ScrapedProductInfo();
        scrapedInfo.setTitle("Test Product");
        scrapedInfo.setDescription("A great product");
        scrapedInfo.setImageUrl("https://example.com/image.jpg");
    }

    @Test
    void createLink_shouldOrchestrateServicesAndSaveLink() {
        // Arrange
        String originalUrl = "https://example.com/product/123";
        String affiliateUrl = "https://example.com/product/123?tag=test-tag";
        String shortUrl = "https://bit.ly/xyz";
        List<String> adVariations = List.of("Ad copy 1", "Ad copy 2");
        LocalDateTime scheduledAt = LocalDateTime.now().plusDays(1);

        when(webScraperService.scrapeProductInfo(originalUrl)).thenReturn(scrapedInfo);
        when(affiliateTransformationService.transform(originalUrl)).thenReturn(affiliateUrl);
        when(bitlyService.shortenUrl(affiliateUrl)).thenReturn(shortUrl);
        when(openAiService.generatePostContent("Test Product", "A great product", ContentType.TWEET)).thenReturn(adVariations);

        // Act
        affiliateLinkService.createLink(originalUrl, ContentType.TWEET, scheduledAt);

        // Assert
        verify(affiliateTransformationService).transform(originalUrl);
        ArgumentCaptor<AffiliateLink> linkCaptor = ArgumentCaptor.forClass(AffiliateLink.class);
        verify(affiliateLinkRepository).save(linkCaptor.capture());

        AffiliateLink savedLink = linkCaptor.getValue();
        assertEquals("Test Product", savedLink.getTitle());
        assertEquals(affiliateUrl, savedLink.getLongUrl()); // Verify the transformed URL is saved
        assertEquals(shortUrl, savedLink.getShortUrl());
        assertEquals("https://example.com/image.jpg", savedLink.getProductImageUrl());
        assertEquals(scheduledAt, savedLink.getScheduledAt());
        assertEquals(2, savedLink.getGeneratedContent().size());
        assertEquals("Ad copy 1", savedLink.getGeneratedContent().get(0).getContent());
    }
}
